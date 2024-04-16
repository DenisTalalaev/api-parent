package by.salary.serviceuser.controller;

import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.entities.UserAgreement;
import by.salary.serviceuser.model.UserAgreementRequestDTO;
import by.salary.serviceuser.model.UserAgreementResponseDTO;
import by.salary.serviceuser.service.UserAgreementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Controller
@RestController
@RequestMapping("/useragreements")
public class UserAgreementsController {

    private final UserAgreementsService userAgreementsService;

    @Autowired
    public UserAgreementsController(UserAgreementsService userAgreementsService) {
        this.userAgreementsService = userAgreementsService;
    }

    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserAgreementResponseDTO> getAllUserAgreements(@PathVariable BigInteger user_id,
                                                               @RequestAttribute String email) {
        return userAgreementsService.getAllUserAgreements(user_id, email);
    }

    @PostMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAgreementResponseDTO createUserAgreement(@RequestBody UserAgreementRequestDTO userAgreementRequestDTO,
                                                        @PathVariable BigInteger user_id,
                                                        @RequestAttribute String email) {
        return userAgreementsService.createUserAgreement(userAgreementRequestDTO, user_id, email);
    }

    @DeleteMapping("/{agreement_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserAgreement( @PathVariable BigInteger agreement_id,
                                     @RequestAttribute String email) {
        userAgreementsService.deleteUserAgreement(agreement_id, email);
    }

}
