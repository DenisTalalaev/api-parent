package by.salary.serviceuser.controller;


import by.salary.serviceuser.entities.Authority;
import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.model.changeemail.ChangeEmailRequestDto;
import by.salary.serviceuser.model.changeemail.ChangeEmailResponseDto;
import by.salary.serviceuser.model.changepassword.AuthenticationChangePasswordRequestDto;
import by.salary.serviceuser.model.changepassword.AuthenticationChangePasswordResponseDto;
import by.salary.serviceuser.model.user.UserPromoteRequestDTO;
import by.salary.serviceuser.model.user.UserRequestDTO;
import by.salary.serviceuser.model.user.UserResponseDTO;
import by.salary.serviceuser.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<UserResponseDTO> getAllUsers(@RequestAttribute String email,
                                             @RequestAttribute List<Permission> permissions) {
        return userService.getAllUsers(email, permissions);
    }

    @GetMapping("/getallmails/{userEmail}")
    @ResponseStatus(HttpStatus.OK)
    public String getAllMails(@PathVariable String userEmail) {
        return userService.getAllMails(userEmail);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getOneUser(@PathVariable BigInteger id) {
        return userService.getOneUser(id);
    }


    @GetMapping("/self")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getUser(@RequestAttribute String email) {
        return userService.getUser(email);
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

    @PutMapping("/promote")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO promoteUser(@RequestBody UserPromoteRequestDTO userPromoteRequestDTO,
                                       @RequestAttribute String email,
                                       @RequestAttribute List<Permission> permissions
    ) {
        return userService.promoteUser(userPromoteRequestDTO, email, permissions);
    }

    @PutMapping("/demote")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO demoteUser(@RequestBody UserPromoteRequestDTO userPromoteRequestDTO,
                                       @RequestAttribute String email,
                                       @RequestAttribute List<Permission> permissions
    ) {
        return userService.demoteUser(userPromoteRequestDTO, email, permissions);
    }

    @PutMapping("/authority/{user_id}/{authority_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO updateUser(@PathVariable BigInteger user_id,
                                      @PathVariable BigInteger authority_id,
                                      @RequestAttribute List<Permission> permissions
    ) {
        return userService.setUserAuthority(user_id, authority_id, permissions);
    }

    @GetMapping("/authority")
    @ResponseStatus(HttpStatus.OK)
    public List<Authority> gelAllAuthorities() {
        return userService.getAllAuthorities();
    }

    @GetMapping("/ispermitted/{email}/{permissionName}")
    @ResponseStatus(HttpStatus.OK)
    public String isPermitted(@PathVariable String email, @PathVariable String permissionName) {
        return userService.isPermitted(email, permissionName);
    }

    @PutMapping("/expire/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO expireUser(@PathVariable BigInteger user_id,
                                      @RequestAttribute List<Permission> permissions,
                                      @RequestAttribute String email
    ) {
        return userService.expireUser(user_id, permissions, email);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO renewUser(@RequestBody UserRequestDTO userRequestDTO,
                                     @RequestAttribute List<Permission> permissions) {
        return userService.updateUser(userRequestDTO, permissions);
    }


    @PutMapping("/change/password")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationChangePasswordResponseDto renewUser(@RequestBody AuthenticationChangePasswordRequestDto authenticationChangePasswordRequestDto
    ) {
        return userService.changePassword(authenticationChangePasswordRequestDto);
    }

    @PutMapping("/change/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> changeEmail(@RequestBody ChangeEmailRequestDto changeEmailRequestDto,
                                      @RequestAttribute String email) {
        return ResponseEntity.ok(userService.changeEmail(changeEmailRequestDto, email));
    }

}
