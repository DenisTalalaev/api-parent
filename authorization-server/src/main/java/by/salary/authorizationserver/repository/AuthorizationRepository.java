package by.salary.authorizationserver.repository;

import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AuthorizationRepository {


    Optional<AuthenticationResponseDto> find(AuthenticationRequestDto authenticationRequestDto);
    RegisterResponseDto save(RegisterRequestDto newUser);

}
