package by.salary.serviceagreement.service;

import by.salary.serviceagreement.entities.Agreement;
import by.salary.serviceagreement.entities.AgreementState;
import by.salary.serviceagreement.entities.AgreementStatesList;
import by.salary.serviceagreement.model.*;
import by.salary.serviceagreement.repository.AgreementRepository;
import by.salary.serviceagreement.repository.AgreementStateListRepository;
import by.salary.serviceagreement.repository.AgreementStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class AgreementService {

    @Autowired
    AgreementRepository agreementRepository;

    @Autowired
    AgreementStateListRepository agreementStateListRepository;

    @Autowired
    AgreementStateRepository agreementStateRepository;

    public AgreementResponseDTO getAgreement(AgreementRequestDTO agreementRequestDTO) {
        return new AgreementResponseDTO(agreementRepository.findById(agreementRequestDTO.getId()).get());
    }

    public AgreementStateListResponseDTO getAgreementList(AgreementStateListRequestDTO agreementStateListRequestDTO, BigInteger list_id) {
        return new AgreementStateListResponseDTO(
                agreementStateListRepository.findById(agreementStateListRequestDTO.getId()).get()
        );
    }

    public AgreementStateResponseDTO getAgreementListsState(AgreementStateRequestDTO agreementStateRequestDTO, BigInteger list_id, BigInteger state_id) {
        return new AgreementStateResponseDTO(
                agreementStateRepository.findById(agreementStateRequestDTO.getId()).get()
        );
    }

    public AgreementResponseDTO createAgreement(AgreementRequestDTO agreementRequestDTO) {
        return new AgreementResponseDTO(
                agreementRepository.save(new Agreement(
                        agreementRequestDTO.getAgreementStatesList()
                ))
        );
    }

    public AgreementStateListResponseDTO createAgreementList(AgreementStateListRequestDTO agreementStateListRequestDTO) {
        return new AgreementStateListResponseDTO(
                agreementStateListRepository.save(new AgreementStatesList(agreementStateListRequestDTO))
        );
    }

    public AgreementStateResponseDTO createAgreementState(AgreementStateRequestDTO agreementStateRequestDTO, BigInteger list_id) {
        return new AgreementStateResponseDTO(
                agreementStateRepository.save(new AgreementState(agreementStateRequestDTO))
        );
    }

    public BigInteger createDefaultAgreementList() {
        return agreementRepository.save(new Agreement()).getId();
    }
}
