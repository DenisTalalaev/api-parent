package by.salary.serviceuser.controller;


import by.salary.serviceuser.model.UserRequestDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.JSqlParserUtils;
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
    private final UserService userService;

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


    @PostMapping("/createUser")
    @ResponseStatus(HttpStatus.CREATED)
    public String createUserByInvitation(@RequestBody UserRequestDTO userRequestDTO) {
        System.out.println(userRequestDTO);
        UserResponseDTO user = userService.createUser(userRequestDTO);
        System.out.println(user);
        return user.getId().toString();
    }

    @PostMapping("/getorganisationid/{email}")
    @ResponseStatus(HttpStatus.OK)
    public String getOrganisationId(@PathVariable String email) {
        return userService.getOrganisationId(email);
    }
    @PostMapping("/getorganisationagreementid/{email}")
    @ResponseStatus(HttpStatus.OK)
    public String getOrganisationAgreementId(@PathVariable String email) {
        return userService.getOrganisationAgreementId(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable BigInteger id) {
        userService.deleteUser(id);
    }
}
