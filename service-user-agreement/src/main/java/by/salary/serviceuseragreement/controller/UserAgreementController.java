package by.salary.serviceuseragreement.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/useragreements")
@AllArgsConstructor
@Controller
public class UserAgreementController {


    @RequestMapping
    public String hello() {
        return "User Agreement Controller";
    }
}
