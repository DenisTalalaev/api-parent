package by.salary.serviceuser.controller;

import by.salary.serviceuser.model.PermissionResponseDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.service.PermissionService;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.ListResourceBundle;

@Controller
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PermissionResponseDTO> getUserPermissions(@PathVariable BigInteger user_id) {
        return permissionService.getUserPermissions(user_id);
    }

    @GetMapping("/users/{permission_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getAllUsersWithPermission(@PathVariable BigInteger permission_id) {
        return permissionService.getAllUsersWithPermission(permission_id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PermissionResponseDTO createPermission(@RequestBody PermissionResponseDTO permissionResponseDTO) {
        return permissionService.createPermission(permissionResponseDTO);
    }

    @PostMapping("/{user_id}/{permission_id}")
    @ResponseStatus(HttpStatus.OK)
    public PermissionResponseDTO addUserPermission(@PathVariable BigInteger user_id, @PathVariable BigInteger permission_id) {
        return permissionService.addUserPermission(user_id, permission_id);
    }

    @DeleteMapping("/{user_id}/{permission_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserPermission(@PathVariable BigInteger user_id, @PathVariable BigInteger permission_id) {
        permissionService.deleteUserPermission(user_id, permission_id);
    }
    @DeleteMapping("/{permission_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserPermission(@PathVariable BigInteger permission_id) {
        permissionService.deletePermission(permission_id);
    }



}
