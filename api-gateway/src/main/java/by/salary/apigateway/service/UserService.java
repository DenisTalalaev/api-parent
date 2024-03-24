package by.salary.apigateway.service;


import by.salary.apigateway.model.UserInfoDTO;

import java.util.Optional;

public interface UserService {


    Optional<UserInfoDTO> findByEmail(String email);

    void save(UserInfoDTO user);
}
