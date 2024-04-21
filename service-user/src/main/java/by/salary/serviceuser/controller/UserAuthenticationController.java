package by.salary.serviceuser.controller;

import by.salary.serviceuser.model.user.authentication.UserAuthenticationRequestDTO;
import by.salary.serviceuser.model.user.authentication.UserAuthenticationResponseDTO;
import by.salary.serviceuser.model.user.organisation.UserJoinOrganisationRequestDTO;
import by.salary.serviceuser.model.user.organisation.UserJoinOrganisationResponseDTO;
import by.salary.serviceuser.model.user.registration.UserRegistrationRequestDTO;
import by.salary.serviceuser.model.user.registration.UserRegistrationResponseDTO;
import by.salary.serviceuser.service.UserAuthenticationService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public UserJoinOrganisationResponseDTO joinOrganisation(@RequestBody UserJoinOrganisationRequestDTO userJoinOrganisationRequestDTO,
                                                            @RequestAttribute String email) {
        return userAuthenticationService.joinOrganisation(userJoinOrganisationRequestDTO, email);
    }

}
