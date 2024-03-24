package by.salary.serviceuser.controller;

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

    @GetMapping("{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserAgreementResponseDTO> getAllUserAgreements(@PathVariable BigInteger user_id) {
        return userAgreementsService.getAllUserAgreements(user_id);
    }

    @PostMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAgreementResponseDTO createUserAgreement(@RequestBody UserAgreementRequestDTO userAgreementRequestDTO, @PathVariable BigInteger user_id) {
        return userAgreementsService.createUserAgreement(userAgreementRequestDTO, user_id);
    }

    @DeleteMapping("/{agreement_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserAgreement( @PathVariable BigInteger agreement_id) {
        userAgreementsService.deleteUserAgreement(agreement_id);
    }

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllUserAgreements(@PathVariable BigInteger user_id) {
        userAgreementsService.deleteUserAgreements(user_id);
    }

}
