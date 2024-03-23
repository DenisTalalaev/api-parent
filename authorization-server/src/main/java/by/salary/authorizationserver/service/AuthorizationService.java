package by.salary.authorizationserver.service;

import by.salary.authorizationserver.exception.UserAlreadyExistsException;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.RegisterDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AuthorizationService {


    public Optional<UserInfoDTO> findByEmail(String email);
    public void save(RegisterDto newUser) throws UserAlreadyExistsException;


}
