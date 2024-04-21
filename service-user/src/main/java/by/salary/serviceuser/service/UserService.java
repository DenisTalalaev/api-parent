package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.*;
import by.salary.serviceuser.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.changepassword.AuthenticationChangePasswordRequestDto;
import by.salary.serviceuser.model.changepassword.AuthenticationChangePasswordResponseDto;
import by.salary.serviceuser.model.user.UserPromoteRequestDTO;
import by.salary.serviceuser.model.user.UserRequestDTO;
import by.salary.serviceuser.model.user.UserResponseDTO;
import by.salary.serviceuser.repository.AuthorityRepository;
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

    AuthorityRepository authorityRepository;
    private OrganisationRepository organisationRepository;

    @Autowired
    public UserService(UserRepository userRepository, OrganisationRepository organisationRepository, AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    public List<UserResponseDTO> getAllUsers(String email) {

        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if (!Permission.isPermitted(optUser.get(), PermissionsEnum.READ_ALL_USERS)) {
            throw new NotEnoughtPermissionsException("User with email " + email + " has not enough permissions", HttpStatus.FORBIDDEN);
        }
        BigInteger organisationId = optUser.get().getOrganisation().getId();

        if (!organisationRepository.existsById(organisationId)) {
            throw new UserNotFoundException("Organisation with id " + organisationId + " not found", HttpStatus.NOT_FOUND);
        }

        List<UserResponseDTO> users = new ArrayList<>();
        userRepository.findAllByOrganisationId(organisationId).forEach(user -> {
            if (user.getIsAccountNonExpired() != null) {
                if (user.getIsAccountNonExpired()) {
                    users.add(new UserResponseDTO(user));
                }
            } else {
                users.add(new UserResponseDTO(user));
            }
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
        user.getAuthorities().add(new Authority(AuthorityEnum.USER));
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
        if (!permissions.contains(new Permission(PermissionsEnum.PROMOTE_USER))) {
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> promoterOpt = userRepository.findByUserEmail(email);
        if (promoterOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        Optional<User> userOpt = userRepository.findByUsername(userPromoteRequestDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User  " + userPromoteRequestDTO.getUsername() + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        user.getAuthorities().add(userPromoteRequestDTO.getAuthority());
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public UserResponseDTO demoteUser(UserPromoteRequestDTO userPromoteRequestDTO, String email, List<Permission> permissions) {
        if (!permissions.contains(new Permission(PermissionsEnum.PROMOTE_USER))) {
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> promoterOpt = userRepository.findByUserEmail(email);
        if (promoterOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        Optional<User> userOpt = userRepository.findByUsername(userPromoteRequestDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User  " + userPromoteRequestDTO.getUsername() + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (user.getAuthorities().contains(userPromoteRequestDTO.getAuthority())) {
            user.getAuthorities().remove(userPromoteRequestDTO.getAuthority());
        } else {
            throw new UserNotFoundException("User  " + userPromoteRequestDTO.getUsername() + " has not this authority", HttpStatus.NOT_FOUND);
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

    private boolean addUserPermissions(User user) {
        userRepository.save(user);
        return true;
    }

    private boolean addModeratorPermissions(User user) {
        addUserPermissions(user);
        user.addPermission(new Permission(PermissionsEnum.CRUD_AGREEMENT_STATE));

        user.addPermission(new Permission(PermissionsEnum.CRUD_AGREEMENT_LIST));

        user.addPermission(new Permission(PermissionsEnum.CRUD_USER_AGREEMENT));

        user.addPermission(new Permission(PermissionsEnum.INVITE_USER));
        user.addPermission(new Permission(PermissionsEnum.EXPIRE_USER));

        userRepository.save(user);
        return true;
    }

    private boolean addAdministratorPermissions(User user) {
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

    public UserResponseDTO setUserAuthority(BigInteger userId, BigInteger authorityId, String email) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (!Permission.isPermitted(user, PermissionsEnum.AUTHORITY_REDACTION)) {
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<Authority> authorityOpt = authorityRepository.findById(authorityId);
        if (authorityOpt.isEmpty()) {
            throw new UserNotFoundException("Authority with id " + authorityId + " not found", HttpStatus.NOT_FOUND);
        }

        Optional<User> userOpt2 = userRepository.findById(userId);
        if (userOpt2.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        User user2 = userOpt2.get();
        Authority authority = authorityOpt.get();
        user2.getAuthorities().clear();
        user2.getAuthorities().add(authority);
        userRepository.save(user2);
        return new UserResponseDTO(user2);
    }

    public String isPermitted(String email, String permission) {
        return String.valueOf(Permission.isPermitted(userRepository.findByUserEmail(email).get(), PermissionsEnum.valueOf(permission)));
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, String email) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (!Permission.isPermitted(user, PermissionsEnum.REDACT_USER_INFO)) {
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> userOpt2 = userRepository.findById(userRequestDTO.getId());
        if (userOpt2.isEmpty()) {
            throw new UserNotFoundException("User with id " + userRequestDTO.getId() + " not found", HttpStatus.NOT_FOUND);
        }
        User user2 = userOpt2.get();
        user2.update(userRequestDTO);
        userRepository.save(user2);
        return new UserResponseDTO(user2);
    }

    public UserResponseDTO expireUser(BigInteger user_id, String email) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (!Permission.isPermitted(user, PermissionsEnum.EXPIRE_USER)) {
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> userOpt2 = userRepository.findById(user_id);
        if (userOpt2.isEmpty()) {
            throw new UserNotFoundException("User with id " + user_id + " not found", HttpStatus.NOT_FOUND);
        }
        User user2 = userOpt2.get();
        user2.getAuthorities().clear();;
        user2.getPermissions().clear();
        user2.setOrganisation(null);

        userRepository.save(user2);
        return new UserResponseDTO(user2);
    }

    public AuthenticationChangePasswordResponseDto changePassword(AuthenticationChangePasswordRequestDto authenticationChangePasswordRequestDto) {
        Optional<User> optionalUser = userRepository.findByUserEmail(authenticationChangePasswordRequestDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + authenticationChangePasswordRequestDto.getEmail() + " not found", HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setUserPassword(authenticationChangePasswordRequestDto.getPassword());
        userRepository.save(user);
        return new AuthenticationChangePasswordResponseDto(HttpStatus.OK, "Password changed successfully");
    }

    public List<Authority> getAllAuthorities() {
        ArrayList<Authority> authorities = new ArrayList<>();
        authorityRepository.findAll().forEach(authority -> authorities.add(authority));
        return authorities;
    }
}
