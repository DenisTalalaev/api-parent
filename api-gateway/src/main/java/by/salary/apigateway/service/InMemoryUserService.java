package by.salary.apigateway.service;

import by.salary.apigateway.model.UserInfoDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InMemoryUserService implements UserService {

    List<UserInfoDTO> repository;

    public InMemoryUserService(){
        repository = new ArrayList<>();
    }

    @Override
    public Optional<UserInfoDTO> findByEmail(String email) {
        return repository.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override
    public void save(UserInfoDTO newUser) {
        Optional<UserInfoDTO> user = repository.stream().filter(u -> u.getEmail().equals(newUser.getEmail())).findFirst();
        if (user.isEmpty())
            repository.add(newUser);
    }

}
