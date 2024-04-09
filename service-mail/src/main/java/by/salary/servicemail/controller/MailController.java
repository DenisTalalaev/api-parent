package by.salary.servicemail.controller;

import by.salary.servicemail.model.MailRequestDTO;
import by.salary.servicemail.model.MailResponseDTO;
import by.salary.servicemail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mail")
@RestController
public class MailController {

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }


    @PostMapping
    @RequestMapping("/notification")
    public MailResponseDTO sendMail(@RequestBody MailRequestDTO mailRequestDTO, @RequestAttribute String email) {
        return mailService.sendMail(mailRequestDTO, email);
    }


    /**
     * endpoint to send mail to all users with specific authority
     */
    @PostMapping
    @RequestMapping("/broadcast")
    public MailResponseDTO broadcastMail(@RequestBody  MailRequestDTO mailRequestDTO, @RequestAttribute String email) {
        return mailService.broadcastMail(mailRequestDTO, email);
    }

    @PostMapping
    @RequestMapping("/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkMail(@RequestBody  MailRequestDTO mailRequestDTO, @RequestAttribute String email) {
        mailService.checkMail(mailRequestDTO);
    }

}
