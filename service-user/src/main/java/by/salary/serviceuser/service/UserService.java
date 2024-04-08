package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Organisation;
import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.UserRequestDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    UserRepository userRepository;
    private OrganisationRepository organisationRepository;

    @Autowired
    public UserService(UserRepository userRepository, OrganisationRepository organisationRepository) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    public List<UserResponseDTO> getAllUsers() {

        List<UserResponseDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            users.add(new UserResponseDTO(user));
        });
        return users;
    }

    public UserResponseDTO getOneUser(BigInteger id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        return new UserResponseDTO(optUser.get());
    }


    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(userRequestDTO.getOrganisationId());
        if (optionalOrganisation.isEmpty()) {
            throw new UserNotFoundException("Organisation with id " + userRequestDTO.getOrganisationId() + " not found", HttpStatus.NOT_FOUND);
        }
        return new UserResponseDTO(userRepository.save(new User(userRequestDTO, optionalOrganisation.get())));
    }

    public void deleteUser(BigInteger id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    public String getOrganisationId(String email) {
        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        return optUser.get().getOrganisation().getId().toString();

    }

    public String getOrganisationAgreementId(String email) {
        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        return optUser.get().getOrganisation().getAgreementId().toString();

    }

    public String getAllMails(String userEmail) {
        Optional<User> optUser = userRepository.findByUserEmail(userEmail);
        if (optUser.isEmpty()) {
            return "nopermissions";
        }
        return String.join("\n",
                userRepository.findAllByOrganisationId(
                        optUser
                                .get()
                                .getOrganisation()
                                .getId())
                        .stream().map(User::getUserEmail)
                        .toList());
    }
}
