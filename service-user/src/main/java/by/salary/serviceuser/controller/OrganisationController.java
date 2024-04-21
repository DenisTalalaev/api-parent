package by.salary.serviceuser.controller;

import by.salary.serviceuser.model.organisation.OrganisationRequestDTO;
import by.salary.serviceuser.model.organisation.OrganisationResponseDTO;
import by.salary.serviceuser.model.user.UserResponseDTO;
import by.salary.serviceuser.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Controller
@RestController
@RequestMapping("/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;

    @Autowired
    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrganisationResponseDTO> getAllOrganisations() {
        return organisationService.getAllOrganisations();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrganisationResponseDTO getOneOrganisation(@PathVariable BigInteger id) {
        return organisationService.getOneOrganisation(id);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getOrganisationUsers(@PathVariable BigInteger id) {
        return organisationService.getOrganisationUsers(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganisationResponseDTO createOrganisation(@RequestBody OrganisationRequestDTO organisationRequestDTO,
                                                      @RequestAttribute String email) {
        return organisationService.createOrganisation(organisationRequestDTO, email);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public OrganisationResponseDTO updateOrganisation(@RequestBody OrganisationRequestDTO organisationRequestDTO,
                                                      @RequestAttribute String email) {
        return organisationService.updateOrganisation(organisationRequestDTO, email);
    }


}
