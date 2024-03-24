package by.salary.authorizationserver.service;


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
    public Optional<UserInfoDTO> save(RegisterDto newUser){
        Optional<UserInfoDTO> user = repository.stream().filter(u -> u.getEmail().equals(newUser.getEmail())).findFirst();
        if (user.isPresent()){
            return Optional.empty();
        }
        UserInfoDTO userInfoDTO = mapToUserInfoDTO(newUser);
        userInfoDTO.setAuthorities(List.of("USER"));
        userInfoDTO.setId(newId++);
        repository.add(userInfoDTO);
        return Optional.of(userInfoDTO);
    }


    private UserInfoDTO mapToUserInfoDTO(RegisterDto registerRequest){
        return UserInfoDTO.builder()
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .authorities(registerRequest.getAuthorities())
                .pictureUri(registerRequest.getPictureUri())
                .registrationId(registerRequest.getAuthenticationRegistrationId().name())
                .build();
    }

}
