package by.salary.authorizationserver.service;

import by.salary.authorizationserver.exception.EmailNotFoundException;
import by.salary.authorizationserver.exception.UserNotFoundException;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.*;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.userrequest.RegisterLocalUserRequest;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.util.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthenticationRegistrationService {

    JwtService jwtService;

    AuthorizationRepository authorizationRepository;

    PasswordEncoder passwordEncoder;

    TokenRegistrationService tokenRegistrationService;

    MailService mailService;

    AuthenticationUtils authenticationUtils;

    LogoutHandler logoutHandler;
    public RegisterResponseDto register(RegisterLocalUserRequest registerLocalUserRequest) {

        MailRequestDTO mailRequestDTO = MailRequestDTO.builder()
                .mailTo(registerLocalUserRequest.getUserEmail())
                .build();

        if (!mailService.checkEmail(mailRequestDTO)){
            throw new EmailNotFoundException("Email " + registerLocalUserRequest.getUserEmail() + " not found");
        }

        registerLocalUserRequest.setPassword(passwordEncoder.encode(registerLocalUserRequest.getPassword()));
        return authorizationRepository.save(mapToRegisterDto(registerLocalUserRequest));
    }


    public ConnValidationResponse authenticate(AuthenticationLocalUserRequest authenticationRequestDto) throws UserNotFoundException {
        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .username(authenticationRequestDto.getUsername())
                .userEmail(authenticationRequestDto.getUserEmail())
                .build();

        Optional<AuthenticationResponseDto> authentication = authorizationRepository.find(authenticationRequest);

        if (authentication.isEmpty()){
            throw new UserNotFoundException("Authentication failed");
        }
        //TODO: verify email

        if (!passwordEncoder.matches(authenticationRequestDto.getPassword(), authentication.get().getPassword())){
            //TODO: exception handling
            throw new UserNotFoundException("Authentication failed");
        }

        String token = tokenRegistrationService.generateToken(mapToUserInfoDto(authentication.get()));

        return mapToConnValidationResponse(authentication.get(), token);
    }

    public AuthenticationChangePasswordResponseDto changePassword(String email, ChangePasswordRequestDto changePasswordRequestDto) {
        AuthenticationChangePasswordRequestDto requestDto = AuthenticationChangePasswordRequestDto.builder()
                .email(email)
                .password(passwordEncoder.encode(changePasswordRequestDto.getPassword()))
                .build();

        return authorizationRepository.changePassword(requestDto);
    }


    private RegisterRequestDto mapToRegisterDto(RegisterLocalUserRequest registerLocalUserRequest){
        return RegisterRequestDto.builder()
                .username(registerLocalUserRequest.getUsername())
                .userEmail(registerLocalUserRequest.getUserEmail())
                .userPassword(registerLocalUserRequest.getPassword())
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .is2FEnabled(registerLocalUserRequest.is2FEnabled())
                .build();
    }


    private ConnValidationResponse mapToConnValidationResponse(AuthenticationResponseDto authentication, String token) {
        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .isAuthenticated(true)
                .methodType("Bearer")
                .email(authentication.getUserEmail())
                .token(token)
                .authorities(authentication.getAuthorities().stream().map(Authority::getAuthority).toList())
                .build();
    }

    private UserInfoDTO mapToUserInfoDto(AuthenticationResponseDto authenticationResponseDto){
        return UserInfoDTO.builder()
                .name(authenticationResponseDto.getUserName())
                .email(authenticationResponseDto.getUserEmail())
                .authorities(authenticationResponseDto.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toList()))
                .is2FEnabled(authenticationResponseDto.is2FEnabled())
                .is2FVerified(false)
                .build();

    }
}
