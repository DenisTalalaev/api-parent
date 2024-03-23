package by.salary.authorizationserver.service;


import by.salary.authorizationserver.exception.UserAlreadyExistsException;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.RegisterDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Slf4j
public class InMemoryAuthorizationService implements AuthorizationService {


    private Long newId = 2L;

    List<UserInfoDTO> repository;

    public InMemoryAuthorizationService(){
        repository = new ArrayList<>();
    }

    @Override
    public Optional<UserInfoDTO> findByEmail(String email) {
        log.info(Arrays.toString(new Exception().getStackTrace()));
        return repository.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override
    public void save(RegisterDto newUser) throws UserAlreadyExistsException {
        Optional<UserInfoDTO> user = repository.stream().filter(u -> u.getEmail().equals(newUser.getEmail())).findFirst();
        if (user.isPresent()){
            throw new UserAlreadyExistsException("User already exists");
        }
        UserInfoDTO userInfoDTO = mapToUserInfoDTO(newUser);
        userInfoDTO.setAuthorities(List.of("USER"));
        userInfoDTO.setId(newId++);
        repository.add(userInfoDTO);
    }


    private UserInfoDTO mapToUserInfoDTO(RegisterDto registerDto){
        return UserInfoDTO.builder()
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .build();
    }

}
