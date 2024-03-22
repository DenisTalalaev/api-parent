package by.salary.serviceuseragreement.controller;


import by.salary.serviceuseragreement.model.UserAgreementRequestDTO;
import by.salary.serviceuseragreement.model.UserAgreementResponseDTO;
import by.salary.serviceuseragreement.service.UserAgreementService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/useragreements")
@Controller
@NoArgsConstructor
public class UserAgreementController {

    private UserAgreementService userAgreementService;

    @Autowired
    public UserAgreementController(UserAgreementService userAgreementService) {
        this.userAgreementService = userAgreementService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserAgreementResponseDTO> getAllUserAgreements() {
        return userAgreementService.getAllUserAgreements();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAgreementResponseDTO getUserAgreementById(@PathVariable BigInteger id) {
        return userAgreementService.getUserAgreementById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserAgreementResponseDTO createUserAgreement(@RequestBody UserAgreementRequestDTO userAgreementRequestDTO) {
        return userAgreementService.createUserAgreement(userAgreementRequestDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserAgreementResponseDTO updateUserAgreement(@RequestBody UserAgreementRequestDTO userAgreementRequestDTO) {
        return userAgreementService.updateUserAgreement(userAgreementRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void deleteUserAgreement(@PathVariable BigInteger id) {
        userAgreementService.deleteUserAgreement(id);
    }

}
