package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.model.UserRequestDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    UserRepository userRepository;
    private OrganisationRepository organisationRepository;

    private WebClient.Builder webClientBuilder;

    @Autowired
    public UserService(UserRepository userRepository, OrganisationRepository organisationRepository, WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.webClientBuilder = webClientBuilder;
    }

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
        return new UserResponseDTO(userRepository.save(new User(userRequestDTO, organisationRepository.findById(userRequestDTO.getOrganisationId()).get())));
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO) {
        Optional<User> user = userRepository.findById(userRequestDTO.getId());
        user.ifPresent(value -> value.update(userRequestDTO, organisationRepository.findById(userRequestDTO.getOrganisationId()).get()));
        return new UserResponseDTO(userRepository.save(user.get()));
    }

    public void deleteUser(BigInteger id) {
        userRepository.deleteById(id);
    }
}
