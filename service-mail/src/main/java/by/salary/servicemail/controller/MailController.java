package by.salary.servicemail.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mail")
@RestController
public class MailController {


    /**
     * endpoint to send mail to one user
     */
    @PostMapping
    @RequestMapping("/notification")
    public void sendMail() {

    }


    /**
     * endpoint to send mail to all users with specific authority
     */
    @PostMapping
    @RequestMapping("/broadcast")
    public void broadcastMail() {

    }

}
