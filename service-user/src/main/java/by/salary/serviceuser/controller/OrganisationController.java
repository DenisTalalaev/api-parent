package by.salary.serviceuser.controller;

import by.salary.serviceuser.model.OrganisationRequestDTO;
import by.salary.serviceuser.model.OrganisationResponseDTO;
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

    @Autowired
    OrganisationService organisationService;


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


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganisationResponseDTO createOrganisation(@RequestBody OrganisationRequestDTO organisationRequestDTO) {
        return organisationService.createOrganisation(organisationRequestDTO);
    }


    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public OrganisationResponseDTO updateOrganisation(@RequestBody OrganisationRequestDTO organisationRequestDTO) {
        return organisationService.updateOrganisation(organisationRequestDTO);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganisation(@PathVariable BigInteger id) {
        organisationService.deleteOrganisation(id);
    }

}
