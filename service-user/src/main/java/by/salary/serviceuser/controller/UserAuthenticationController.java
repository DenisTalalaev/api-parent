package by.salary.serviceuser.controller;

import by.salary.serviceuser.entities.Organisation;
import by.salary.serviceuser.model.*;
import by.salary.serviceuser.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@NoArgsConstructor
@RequestMapping("/account")
public class UserAuthenticationController {

    private UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }


    @PostMapping("/auth")
    public UserAuthenticationResponseDTO auth(@RequestBody UserAuthenticationRequestDTO userAuthenticationRequestDTO) {
        return userAuthenticationService.auth(userAuthenticationRequestDTO);
    }

    @PostMapping("/reg")
    public UserRegistrationResponseDTO reg(@RequestBody UserRegistrationRequestDTO userRegistrationRequestDTO) {
        return userAuthenticationService.reg(userRegistrationRequestDTO);
    }

    @PostMapping("/joinorganisation")
    public UserJoinOrganisationResponseDTO joinOrganisation(@RequestBody UserJoinOrganisationRequestDTO userJoinOrganisationRequestDTO) {
        return userAuthenticationService.joinOrganisation(userJoinOrganisationRequestDTO);
    }

}
