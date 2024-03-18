package by.salary.serveruser.controllers;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @GetMapping("/hello")
    public String hello(){


        return "Hello";
    }

}
