package by.salary.authorizationserver.service;

import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AuthorizationService {


    Optional<UserInfoDTO> findByEmail(String email);
    Optional<UserInfoDTO> save(RegisterRequestDto newUser);


}
