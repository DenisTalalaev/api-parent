package by.salary.serviceagreement.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@AllArgsConstructor
@RequestMapping("/agreements")
public class AgreementController {

    @GetMapping
    public String index() {
        return "Agreement service";
    }
}
