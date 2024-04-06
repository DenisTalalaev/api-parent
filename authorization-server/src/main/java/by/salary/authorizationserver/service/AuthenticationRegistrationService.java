package by.salary.authorizationserver.service;

import by.salary.authorizationserver.exception.UserNotFoundException;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.dto.*;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.userrequest.RegisterLocalUserRequest;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationRegistrationService {

    JwtService jwtService;

    AuthorizationRepository authorizationRepository;

    PasswordEncoder passwordEncoder;

    public RegisterResponseDto register(RegisterLocalUserRequest registerLocalUserRequest) {
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

        if (!passwordEncoder.matches(authenticationRequestDto.getPassword(), authentication.get().getPassword())){
            //TODO: exception handling
            throw new UserNotFoundException("Authentication failed");
        }

        return mapToConnValidationResponse(authentication.get());
    }


    private RegisterRequestDto mapToRegisterDto(RegisterLocalUserRequest registerLocalUserRequest){
        return RegisterRequestDto.builder()
                .username(registerLocalUserRequest.getUsername())
                .userEmail(registerLocalUserRequest.getUserEmail())
                .userPassword(registerLocalUserRequest.getPassword())
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .invitationCode(registerLocalUserRequest.getInvitationCode())
                .build();
    }


    private ConnValidationResponse mapToConnValidationResponse(AuthenticationResponseDto authentication) {
        return mapToConnValidationResponse(authentication, jwtService.generateToken(authentication));
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
}
