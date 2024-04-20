package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.entities.PermissionsEnum;
import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceuser.exceptions.PermissionNotFoundException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.PermissionResponseDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.PermissionRepository;
import by.salary.serviceuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    PermissionRepository permissionRepository;
    UserRepository userRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;

        for(PermissionsEnum permission : PermissionsEnum.values()){
            if(!permissionRepository.existsByName(permission.name())){
                permissionRepository.save(new Permission(permission));
            }
        }
    }

    public List<PermissionResponseDTO> getUserPermissions(BigInteger id) {

        List<PermissionResponseDTO> permissions = new ArrayList<>();

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException("User with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        optionalUser.get().getPermissions().forEach(permission -> {
            permissions.add(new PermissionResponseDTO(permission));
        });
        return permissions;

    }

    public List<UserResponseDTO> getAllUsersWithPermission(BigInteger permissionId) {
        if(!permissionRepository.existsById(permissionId)){
            throw new PermissionNotFoundException("Permission with id " + permissionId + " not found", HttpStatus.NOT_FOUND);
        }

        List<UserResponseDTO> users = new ArrayList<>();
        permissionRepository.findById(permissionId).get().getUsers().forEach(user -> {
            users.add(new UserResponseDTO(user));
        });
        return users;
    }

    public List<PermissionResponseDTO> getAllPermissions() {
        List<PermissionResponseDTO> permissions = new ArrayList<>();
        permissionRepository.findAll().forEach(permission -> {
            permissions.add(new PermissionResponseDTO(permission));
        });
        return permissions;
    }


    public PermissionResponseDTO createPermission(PermissionResponseDTO permissionResponseDTO) {
        return new PermissionResponseDTO(permissionRepository.save(new Permission(permissionResponseDTO) ));
    }

    public PermissionResponseDTO addUserPermission(BigInteger userId, BigInteger permissionId, String email, List<Permission> permissions) {

        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        if(!userRepository.existsByUserEmail(email)){
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if(!permissionRepository.existsById(permissionId)){
            throw new PermissionNotFoundException("Permission with id " + permissionId + " not found", HttpStatus.NOT_FOUND);
        }
        if(!permissions.contains(new Permission(PermissionsEnum.PROMOTE_USER)) && !permissions.contains(new Permission(PermissionsEnum.ALL_PERMISSIONS))){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if(!userRepository.findByUserEmail(email).get().getOrganisation().getId().equals(
                userRepository.findById(userId).get().getOrganisation().getId()
        )){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Permission permission = permissionRepository.findById(permissionId).get();
        permission.getUsers().add(userRepository.findById(userId).get());
        return new PermissionResponseDTO(permissionRepository.save(permission));
    }

    public void deleteUserPermission(BigInteger userId, BigInteger permissionId, String email, List<Permission> permissions) {
        Optional<User> optUser = userRepository.findById(userId);
        Optional<Permission> optPermission = permissionRepository.findById(permissionId);

        if(optUser.isEmpty()){
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        if(optPermission.isEmpty()){
            throw new PermissionNotFoundException("Permission with id " + permissionId + " not found", HttpStatus.NOT_FOUND);
        }


        if(!permissions.contains(new Permission(PermissionsEnum.DEMOTE_USER)) && !permissions.contains(new Permission(PermissionsEnum.ALL_PERMISSIONS))){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if(!userRepository.findByUserEmail(email).get().getOrganisation().getId().equals(
                userRepository.findById(userId).get().getOrganisation().getId()
        )){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }

        User user = optUser.get();
        user.getPermissions().remove(optPermission.get());
        userRepository.save(user);
    }

    public List<PermissionResponseDTO> getUserPermissions(String email) {
        Optional<User> optUser = userRepository.findByUserEmail(email);
        if(optUser.isEmpty()){
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        List<PermissionResponseDTO> permissions = new ArrayList<>();
        optUser.get().getPermissions().forEach(permission -> {
            permissions.add(new PermissionResponseDTO(permission));
        });
        return permissions;
    }
}
