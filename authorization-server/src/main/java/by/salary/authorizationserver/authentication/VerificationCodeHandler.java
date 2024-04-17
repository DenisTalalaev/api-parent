package by.salary.authorizationserver.authentication;

import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.TokenEntity;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.userrequest.VerificationCodeRequest;
import by.salary.authorizationserver.repository.TokenRepository;
import by.salary.authorizationserver.service.JwtService;
import by.salary.authorizationserver.service.TokenRegistrationService;
import by.salary.authorizationserver.service.TokenService;
import by.salary.authorizationserver.util.AuthenticationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class VerificationCodeHandler {

    TokenService tokenService;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;

    TokenRegistrationService tokenRegistrationService;

    public boolean verify(String jwt, String verificationCode) {
        String username = jwtService.extractUserName(jwt);

        List<TokenEntity> tokens = tokenService.findAllByUsername(username);
        if (tokens.isEmpty()){
            throw new AuthenticationCredentialsNotFoundException("Jwt is not presented in data base");
        }
        for (TokenEntity token : tokens){
            if (passwordEncoder.matches(jwt, token.getAuthenticationToken())){
                if (verificationCode.equals(token.getVerificationCode())) {
                    tokenService.deleteById(token.getId(), token.getUsername());
                    return true;
                }
            }
        }
        return false;
    }

    public ConnValidationResponse generateNewToken(UserInfoDTO userInfoDTO) {
        String token = tokenRegistrationService.generateToken(userInfoDTO);

        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .isAuthenticated(true)
                .methodType("Bearer")
                .token(token)
                .email(userInfoDTO.getEmail())
                .token(token)
                .authorities(userInfoDTO.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .is2FEnabled(userInfoDTO.is2FEnabled())
                .is2FVerified(true)
                .build();
    }

    private UserInfoDTO mapToUserDetails(JwtAuthenticationToken authentication) {
        return UserInfoDTO.builder()
                .name(authentication.getUsername())
                .email(authentication.getEmail())
                .is2FEnabled(authentication.is2FEnabled())
                .is2FVerified(true)
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .build();
    }
}
