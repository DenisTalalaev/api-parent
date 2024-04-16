package by.salary.serviceinvitation.controller;

import by.salary.serviceinvitation.filters.Permission;
import by.salary.serviceinvitation.model.InvitationRequestDTO;
import by.salary.serviceinvitation.model.InvitationResponseDTO;
import by.salary.serviceinvitation.service.InvitationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.URIParameter;
import java.util.List;

@RestController
@Controller
@RequestMapping("/invitations")
@AllArgsConstructor
public class InvitationController {

    @Autowired
    InvitationService invitationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InvitationResponseDTO> getInvitations() {
        return invitationService.getAllInvitations();
    }

    @GetMapping("/{invitationCode}")
    @ResponseStatus(HttpStatus.OK)
    public String getInvitations(@PathVariable String invitationCode) {
        return invitationService.getInvitation(invitationCode);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponseDTO createInvitation(@RequestBody InvitationRequestDTO invitationRequestDTO,
                                                  @RequestAttribute String email) {
        return invitationService.createInvitation(invitationRequestDTO, email);
    }

    @DeleteMapping("/{invitationCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public InvitationResponseDTO deleteInvitation(@PathVariable String invitationCode) {
        return invitationService.deleteInvitation(invitationCode);
    }


}
