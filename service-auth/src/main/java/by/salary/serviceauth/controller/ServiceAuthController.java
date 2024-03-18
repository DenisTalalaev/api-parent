package by.salary.serviceauth.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Controller
@RequestMapping("/auth")
public class ServiceAuthController {

    @GetMapping
    public String index() {
        return "Auth service";
    }
}
