package by.salary.serviceinvitation.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@RequestMapping("/invitations")
@AllArgsConstructor
public class InvitationController {

    @GetMapping
    public String hello(){
        return "Invitation Controller";
    }
}
