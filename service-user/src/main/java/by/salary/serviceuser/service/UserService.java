package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.model.UserRequestDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {

        List<UserResponseDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            users.add(new UserResponseDTO(user));
        });
        return users;
    }

    public UserResponseDTO getOneUser(BigInteger id) {
        return new UserResponseDTO(userRepository.findById(id).get());
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        return new UserResponseDTO(userRepository.save(new User(userRequestDTO)));
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userRequestDTO.getId()).get();
        if (user != null) {
            user.update(userRequestDTO);
        }
        return new UserResponseDTO(userRepository.save(user));
    }

    public void deleteUser(BigInteger id) {
        userRepository.deleteById(id);
    }
}
