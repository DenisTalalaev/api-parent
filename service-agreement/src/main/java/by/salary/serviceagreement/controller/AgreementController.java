package by.salary.serviceagreement.controller;

import by.salary.serviceagreement.filters.Permission;
import by.salary.serviceagreement.model.*;
import by.salary.serviceagreement.service.AgreementService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@Controller
@RequestMapping("/agreements")
public class AgreementController {


    private final AgreementService agreementService;

    public AgreementController(AgreementService agreementService) {
        this.agreementService = agreementService;
    }


    @GetMapping("/getagreementstate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getAgreementState(@PathVariable BigInteger id) {
        return agreementService.getAgreementState(id);
    }


    @GetMapping()
    public AgreementResponseDTO getAgreement(@RequestAttribute String email) {
        return new AgreementResponseDTO(agreementService.getAgreement(email));
    }


    @PostMapping("/createdefaultagreement")
    public String createDefaultAgreement() {
        return agreementService.createDefaultAgreementList().toString();
    }

    @PostMapping
    public AgreementListResponseDTO createAgreementList(@RequestBody AgreementListRequestDTO agreementListResponseDTO,
                                                        @RequestAttribute String email) {
        return new AgreementListResponseDTO(agreementService.createAgreementList(agreementListResponseDTO, email));
    }

    @PostMapping("/{list_id}")
    public AgreementStateResponseDTO createAgreementState(@RequestBody AgreementStateRequestDTO agreementStateListRequestDTO,
                                                          @PathVariable BigInteger list_id,
                                                          @RequestAttribute String email) {
        return new AgreementStateResponseDTO(agreementService.createAgreementState(agreementStateListRequestDTO, list_id, email));
    }

    @DeleteMapping("/{list_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAgreementList(@PathVariable BigInteger list_id,
                                    @RequestAttribute String email) {
        agreementService.deleteAgreementList(list_id, email);
    }

    @DeleteMapping("/{list_id}/{state_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAgreementState(@PathVariable BigInteger list_id,
                                      @PathVariable BigInteger state_id,
                                      @RequestAttribute String email) {
        agreementService.deleteAgreementState(list_id, state_id, email);
    }

    @PutMapping("/{list_id}/{state_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAgreementState(@PathVariable BigInteger list_id,
                                      @PathVariable BigInteger state_id,
                                      @RequestBody AgreementStateRequestDTO agreementStateListRequestDTO,
                                      @RequestAttribute String email) {
        agreementService.updateAgreementState(agreementStateListRequestDTO, list_id, state_id, email);
    }

    @PutMapping("/{list_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAgreementList(@PathVariable BigInteger list_id,
                                     @RequestBody AgreementListRequestDTO agreementListResponseDTO,
                                     @RequestAttribute String email) {
        agreementService.updateAgreementList(agreementListResponseDTO, list_id, email);
    }






}
