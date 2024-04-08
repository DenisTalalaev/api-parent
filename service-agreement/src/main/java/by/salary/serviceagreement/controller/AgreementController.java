package by.salary.serviceagreement.controller;

import by.salary.serviceagreement.model.*;
import by.salary.serviceagreement.service.AgreementService;
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

    @GetMapping("/admin")
    public List<AgreementResponseDTO> getAgreements() {
        return agreementService.getAllAgreements();
    }

    @GetMapping("/admin/new")
    public AgreementResponseDTO newAgreement() {
        return agreementService.createAgreement();
    }
    @GetMapping("/admin/new/{agreement_id}")
    public AgreementListResponseDTO newAgreement(@PathVariable BigInteger agreement_id, @RequestBody AgreementListRequestDTO agreementStateListRequestDTO) {
        return agreementService.createAgreementList(agreementStateListRequestDTO, agreement_id);
    }

    @GetMapping("/admin/new/{agreement_id}/{list_id}")
    public AgreementStateResponseDTO newAgreementState(@PathVariable BigInteger agreement_id, @PathVariable BigInteger list_id, @RequestBody AgreementStateRequestDTO agreementStateRequestDTO) {
        return agreementService.createAgreementState(agreementStateRequestDTO, agreement_id, list_id);
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
    public AgreementListResponseDTO createAgreementList(@RequestBody AgreementListRequestDTO agreementListResponseDTO, @RequestAttribute String email) {
        return new AgreementListResponseDTO(agreementService.createAgreementList(agreementListResponseDTO, email));
    }

    @PostMapping("/{list_id}")
    public AgreementStateResponseDTO createAgreementState(@RequestBody AgreementStateRequestDTO agreementStateListRequestDTO, @PathVariable BigInteger list_id, @RequestAttribute String email) {
        return new AgreementStateResponseDTO(agreementService.createAgreementState(agreementStateListRequestDTO, list_id, email));
    }




}
