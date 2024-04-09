package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.*;
import by.salary.serviceuser.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.UserPromoteRequestDTO;
import by.salary.serviceuser.model.UserRequestDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        User user = new User(userRequestDTO, optionalOrganisation.get());
        user.getAuthorities().add(new Authority("USER"));
        return new UserResponseDTO(userRepository.save(user));
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

    public UserResponseDTO promoteUser(UserPromoteRequestDTO userPromoteRequestDTO, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.PROMOTE_USER))){
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> promoterOpt = userRepository.findByUserEmail(email);
        if (promoterOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        Optional<User> userOpt = userRepository.findByUsername(userPromoteRequestDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User  " +userPromoteRequestDTO.getUsername() + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        user.getAuthorities().add(userPromoteRequestDTO.getAuthority());
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public UserResponseDTO demoteUser(UserPromoteRequestDTO userPromoteRequestDTO, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.PROMOTE_USER))){
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> promoterOpt = userRepository.findByUserEmail(email);
        if (promoterOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        Optional<User> userOpt = userRepository.findByUsername(userPromoteRequestDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User  " +userPromoteRequestDTO.getUsername() + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if(user.getAuthorities().contains(userPromoteRequestDTO.getAuthority())){
            user.getAuthorities().remove(userPromoteRequestDTO.getAuthority());
        } else {
            throw new UserNotFoundException("User  " +userPromoteRequestDTO.getUsername() + " has not this authority", HttpStatus.NOT_FOUND);
        }

        clearPermissions(user);

        switch (userPromoteRequestDTO.getAuthority().getAuthority()) {
            case "ADMINISTRATOR" -> addAdministratorPermissions(user);
            case "MODERATOR" -> addModeratorPermissions(user);
            case "USER" -> addUserPermissions(user);
            default -> clearPermissions(user);
        }

        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    private boolean addUserPermissions (User user) {
        user.addPermission(new Permission(PermissionsEnum.READ_OWN_AGREEMENT_STATES));
        user.addPermission(new Permission(PermissionsEnum.READ_AGREEMENT));
        userRepository.save(user);
        return true;
    }

    private boolean addModeratorPermissions (User user) {
        addUserPermissions(user);
        user.addPermission(new Permission(PermissionsEnum.CREATE_AGREEMENT_STATE));
        user.addPermission(new Permission(PermissionsEnum.UPDATE_AGREEMENT_STATE));
        user.addPermission(new Permission(PermissionsEnum.DELETE_AGREEMENT_STATE));

        user.addPermission(new Permission(PermissionsEnum.CREATE_AGREEMENT_LIST));
        user.addPermission(new Permission(PermissionsEnum.UPDATE_AGREEMENT_LIST));
        user.addPermission(new Permission(PermissionsEnum.DELETE_AGREEMENT_LIST));

        user.addPermission(new Permission(PermissionsEnum.READ_USER_AGREEMENT));
        user.addPermission(new Permission(PermissionsEnum.ADD_USER_AGREEMENT));
        user.addPermission(new Permission(PermissionsEnum.DELETE_USER_AGREEMENT));
        user.addPermission(new Permission(PermissionsEnum.CHANGE_USER_AGREEMENT));

        user.addPermission(new Permission(PermissionsEnum.INVITE_USER));
        user.addPermission(new Permission(PermissionsEnum.EXPIRE_USER));

        userRepository.save(user);
        return true;
    }

    private boolean addAdministratorPermissions (User user) {
        user.addPermission(new Permission(PermissionsEnum.ALL_PERMISSIONS));
        userRepository.save(user);
        return true;
    }

    private boolean clearPermissions(User user) {
        user.getPermissions().clear();
        userRepository.save(user);
        return true;
    }

    public UserResponseDTO getUser(String email) {
        return new UserResponseDTO(userRepository.findByUserEmail(email).get());
    }
}
