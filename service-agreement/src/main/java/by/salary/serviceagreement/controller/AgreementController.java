package by.salary.serviceagreement.controller;

import by.salary.serviceagreement.model.*;
import by.salary.serviceagreement.service.AgreementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@Controller
@RequestMapping("/agreements")
public class AgreementController {


    private final AgreementService agreementService;

    public AgreementController(AgreementService agreementService) {
        this.agreementService = agreementService;
    }

    @GetMapping()
    public AgreementResponseDTO getAgreement(AgreementRequestDTO agreementRequestDTO) {
        return agreementService.getAgreement(agreementRequestDTO);
    }

    @GetMapping("/{list_id}")
    public AgreementStateListResponseDTO getAgreementStateList(AgreementStateListRequestDTO agreementStateListRequestDTO, @PathVariable BigInteger list_id) {
        return agreementService.getAgreementList(agreementStateListRequestDTO, list_id);

    }

    @GetMapping("/{list_id}/{state_id}")
    public AgreementStateResponseDTO getAgreementState(AgreementStateRequestDTO agreementStateRequestDTO, @PathVariable BigInteger list_id, @PathVariable BigInteger state_id) {
        return agreementService.getAgreementListsState(agreementStateRequestDTO, list_id, state_id);
    }

    @PostMapping("/")
    public AgreementStateListResponseDTO createAgreementStateList(AgreementStateListRequestDTO agreementStateListRequestDTO) {
        return agreementService.createAgreementList(agreementStateListRequestDTO);
    }

    @PostMapping("/createdefaultagreement")
    public String createDefaultAgreement() {
        return agreementService.createDefaultAgreementList().toString();
    }

    @PostMapping("/{list_id}/{state_id}")
    public AgreementStateResponseDTO createAgreementState(AgreementStateRequestDTO agreementStateRequestDTO, @PathVariable BigInteger list_id) {
        return agreementService.createAgreementState(agreementStateRequestDTO, list_id);
    }

}
