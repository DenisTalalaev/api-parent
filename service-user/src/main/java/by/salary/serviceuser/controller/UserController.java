package by.salary.serviceuser.controller;


import by.salary.serviceuser.model.UserRequestDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Controller
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getOneUser(@PathVariable BigInteger id) {
        return userService.getOneUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO updateUser(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(userRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable BigInteger id) {
        userService.deleteUser(id);
    }
}
