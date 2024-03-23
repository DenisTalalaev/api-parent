package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.model.PermissionResponseDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.PermissionRepository;
import by.salary.serviceuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService {

    PermissionRepository permissionRepository;
    UserRepository userRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    public List<PermissionResponseDTO> getUserPermissions(BigInteger id) {

        List<PermissionResponseDTO> permissions = new ArrayList<>();
        userRepository.findById(id).get().getPermissions().forEach(permission -> {
            permissions.add(new PermissionResponseDTO(permission));
        });
        return permissions;

    }

    public List<UserResponseDTO> getAllUsersWithPermission(BigInteger permissionId) {

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

    public PermissionResponseDTO addUserPermission(BigInteger userId, BigInteger permissionId) {
        Permission permission = permissionRepository.findById(permissionId).get();
        permission.getUsers().add(userRepository.findById(userId).get());
        return new PermissionResponseDTO(permissionRepository.save(permission));
    }

    public void deleteUserPermission(BigInteger userId, BigInteger permissionId) {

        Permission permission = permissionRepository.findById(permissionId).get();
        permission.getUsers().remove(userRepository.findById(userId).get());
        permissionRepository.save(permission);
    }

    public void deletePermission(BigInteger permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
