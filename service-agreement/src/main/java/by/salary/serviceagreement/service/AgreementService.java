package by.salary.serviceagreement.service;

import by.salary.serviceagreement.entities.Agreement;
import by.salary.serviceagreement.entities.AgreementList;
import by.salary.serviceagreement.entities.AgreementState;
import by.salary.serviceagreement.exceptions.AgreementNotFoundException;
import by.salary.serviceagreement.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceagreement.filters.Permission;
import by.salary.serviceagreement.filters.PermissionsEnum;
import by.salary.serviceagreement.model.*;
import by.salary.serviceagreement.repository.AgreementRepository;
import by.salary.serviceagreement.repository.AgreementListRepository;
import by.salary.serviceagreement.repository.AgreementStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.*;

@Service
public class AgreementService {

    AgreementRepository agreementRepository;
    AgreementListRepository agreementListRepository;
    AgreementStateRepository agreementStateRepository;


    WebClient.Builder webClientBuilder;

    @Autowired
    public AgreementService(AgreementRepository agreementRepository, AgreementListRepository agreementListRepository, AgreementStateRepository agreementStateRepository, WebClient.Builder webClientBuilder) {
        this.agreementRepository = agreementRepository;
        this.agreementListRepository = agreementListRepository;
        this.agreementStateRepository = agreementStateRepository;
        this.webClientBuilder = webClientBuilder;
    }


    private BigInteger getAgreementId(String email) {

        return new BigInteger(
                Objects.requireNonNull(webClientBuilder
                        .build()
                        .post()
                        .uri("http://service-user:8080/users/getorganisationagreementid/" + email)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block())

        );

    }

    public List<AgreementResponseDTO> getAllAgreements() {
        ArrayList<AgreementResponseDTO> agreementResponseDTOS = new ArrayList<>();
        for (Agreement agreement : agreementRepository.findAll()) {
            agreementResponseDTOS.add(new AgreementResponseDTO(agreement));
        }
        return agreementResponseDTOS;

    }

    public AgreementResponseDTO createAgreement() {
        Agreement agreement = new Agreement();
        agreementRepository.save(agreement);
        return new AgreementResponseDTO(agreement);
    }

    public AgreementListResponseDTO createAgreementList(AgreementListRequestDTO agreementStateListRequestDTO, BigInteger agreementId) {

        Agreement agreement = agreementRepository.findById(agreementId).get();
        AgreementList agreementList = new AgreementList(agreementStateListRequestDTO.getStateListName(), agreement);
        agreementListRepository.save(agreementList);
        return new AgreementListResponseDTO(agreementList);
    }

    public AgreementStateResponseDTO createAgreementState(AgreementStateRequestDTO agreementStateRequestDTO, BigInteger agreementId, BigInteger listId) {
        AgreementList agreementList = agreementListRepository.findById(listId).get();
        AgreementState agreementState = new AgreementState(agreementStateRequestDTO.getStateName(), agreementStateRequestDTO.getStateInfo(), agreementList);
        agreementStateRepository.save(agreementState);
        return new AgreementStateResponseDTO(agreementState);
    }

    public Agreement getAgreement(String email) {
        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optionalAgreement = agreementRepository.findById(agreementId);
        if (optionalAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        return optionalAgreement.get();
    }

    public BigInteger createDefaultAgreementList() {
        Agreement agreement = new Agreement();
        agreementRepository.save(agreement);
        return agreement.getId();
    }

    public AgreementList createAgreementList(AgreementListRequestDTO agreementListResponseDTO, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.CREATE_AGREEMENT_LIST))) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optAgreement = agreementRepository.findById(agreementId);
        if (optAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        Agreement agreement = optAgreement.get();
        AgreementList agreementList = new AgreementList(agreementListResponseDTO.getStateListName(), agreement);
        agreementListRepository.save(agreementList);
        return agreementList;
    }

    public AgreementState createAgreementState(AgreementStateRequestDTO agreementStateListRequestDTO, BigInteger listId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.CREATE_AGREEMENT_STATE))) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optAgreement = agreementRepository.findById(agreementId);
        if (optAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        Agreement agreement = optAgreement.get();
        Optional<AgreementList> optAgreementList = agreementListRepository.findById(listId);
        if (optAgreementList.isEmpty()) {
            throw new AgreementNotFoundException("Agreement list not found", HttpStatus.NOT_FOUND);
        }
        AgreementList agreementList = optAgreementList.get();
        if (!agreementId.equals(agreementList.getAgreement().getId())) {
            throw new AgreementNotFoundException("Agreement list not found in this organisation", HttpStatus.NOT_FOUND);
        }
        AgreementState agreementState = new AgreementState(agreementStateListRequestDTO, agreementList);
        agreementStateRepository.save(agreementState);
        return agreementState;
    }

    public void deleteAgreementList(BigInteger listId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.DELETE_AGREEMENT_LIST))) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optAgreement = agreementRepository.findById(agreementId);
        if (optAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        Agreement agreement = optAgreement.get();
        Optional<AgreementList> optAgreementList = agreementListRepository.findById(listId);
        if (optAgreementList.isEmpty()) {
            throw new AgreementNotFoundException("Agreement list not found", HttpStatus.NOT_FOUND);
        }
        AgreementList agreementList = optAgreementList.get();
        if (!agreementId.equals(agreementList.getAgreement().getId())) {
            throw new AgreementNotFoundException("Agreement list not found in this organisation", HttpStatus.NOT_FOUND);
        }
        agreementListRepository.delete(agreementList);
    }

    public void deleteAgreementState(BigInteger listId, BigInteger stateId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.DELETE_AGREEMENT_STATE))) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }

        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optAgreement = agreementRepository.findById(agreementId);
        if (optAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        Agreement agreement = optAgreement.get();
        Optional<AgreementList> optAgreementList = agreementListRepository.findById(listId);
        if (optAgreementList.isEmpty()) {
            throw new AgreementNotFoundException("Agreement list not found", HttpStatus.NOT_FOUND);
        }
        AgreementList agreementList = optAgreementList.get();
        if (!agreementId.equals(agreementList.getAgreement().getId())) {
            throw new AgreementNotFoundException("Agreement list not found in this organisation", HttpStatus.NOT_FOUND);
        }
        Optional<AgreementState> optAgreementState = agreementStateRepository.findById(stateId);
        if (optAgreementState.isEmpty()) {
            throw new AgreementNotFoundException("Agreement state not found", HttpStatus.NOT_FOUND);
        }
        AgreementState agreementState = optAgreementState.get();
        if (!listId.equals(agreementState.getAgreementLists().getId())) {
            throw new AgreementNotFoundException("Agreement state not found in this list", HttpStatus.NOT_FOUND);
        }
        agreementStateRepository.delete(agreementState);
    }

    public void updateAgreementState(AgreementStateRequestDTO agreementStateListRequestDTO, BigInteger listId, BigInteger stateId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.UPDATE_AGREEMENT_STATE))) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }

        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optAgreement = agreementRepository.findById(agreementId);
        if (optAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        Agreement agreement = optAgreement.get();
        Optional<AgreementList> optAgreementList = agreementListRepository.findById(listId);
        if (optAgreementList.isEmpty()) {
            throw new AgreementNotFoundException("Agreement list not found", HttpStatus.NOT_FOUND);
        }
        AgreementList agreementList = optAgreementList.get();
        if (!agreementId.equals(agreementList.getAgreement().getId())) {
            throw new AgreementNotFoundException("Agreement list not found in this organisation", HttpStatus.NOT_FOUND);
        }
        Optional<AgreementState> optAgreementState = agreementStateRepository.findById(stateId);
        if (optAgreementState.isEmpty()) {
            throw new AgreementNotFoundException("Agreement state not found", HttpStatus.NOT_FOUND);
        }
        AgreementState agreementState = optAgreementState.get();
        if (!listId.equals(agreementState.getAgreementLists().getId())) {
            throw new AgreementNotFoundException("Agreement state not found in this list", HttpStatus.NOT_FOUND);
        }
        agreementStateRepository.save(agreementState.update(agreementStateListRequestDTO));
    }

    public void updateAgreementList(AgreementListRequestDTO agreementListResponseDTO, BigInteger listId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.UPDATE_AGREEMENT_LIST))) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        BigInteger agreementId = getAgreementId(email);
        Optional<Agreement> optAgreement = agreementRepository.findById(agreementId);
        if (optAgreement.isEmpty()) {
            throw new AgreementNotFoundException("Agreement not found", HttpStatus.NOT_FOUND);
        }
        Agreement agreement = optAgreement.get();
        Optional<AgreementList> optAgreementList = agreementListRepository.findById(listId);
        if (optAgreementList.isEmpty()) {
            throw new AgreementNotFoundException("Agreement list not found", HttpStatus.NOT_FOUND);
        }
        AgreementList agreementList = optAgreementList.get();
        if (!agreementId.equals(agreementList.getAgreement().getId())) {
            throw new AgreementNotFoundException("Agreement list not found in this organisation", HttpStatus.NOT_FOUND);
        }
        agreementListRepository.save(agreementList.update(agreementListResponseDTO));
    }
}
