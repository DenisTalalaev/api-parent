package by.salary.authorizationserver.repository;

import by.salary.authorizationserver.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AuthorizationRepository {


    Optional<AuthenticationResponseDto> find(AuthenticationRequestDto authenticationRequestDto);
    RegisterResponseDto save(RegisterRequestDto newUser);

    AuthenticationChangePasswordResponseDto changePassword(AuthenticationChangePasswordRequestDto changePasswordRequestDto);

}
