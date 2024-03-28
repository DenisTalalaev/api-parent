package by.salary.authorizationserver.service;

import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import by.salary.authorizationserver.model.dto.RegisterRequest;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RegistrationService {


    AuthorizationService authorizationService;

    PasswordEncoder passwordEncoder;

    public RegisterResponseDto register(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return mapToRegisterDto(authorizationService.save(mapToRegisterDto(registerRequest)));
    }


    private RegisterRequestDto mapToRegisterDto(RegisterRequest registerRequest){
        return RegisterRequestDto.builder()
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .build();

    }

    private RegisterResponseDto mapToRegisterDto(Optional<UserInfoDTO> userInfoDTO) {

        return RegisterResponseDto.builder()
                .isRegistrationCompleted(userInfoDTO.isPresent())
                .message(getMessage(userInfoDTO))
                .build();
    }

    private String getMessage(Optional<UserInfoDTO> userInfoDTO) {
        if(userInfoDTO.isEmpty()){
            return "Registration failed";
        }
        return "Registration completed";
    }

}
