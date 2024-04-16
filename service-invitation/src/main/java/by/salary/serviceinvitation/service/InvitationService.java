package by.salary.serviceinvitation.service;

import by.salary.serviceinvitation.entities.Invitation;
import by.salary.serviceinvitation.exceptions.InvitationNotFoundException;
import by.salary.serviceinvitation.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceinvitation.filters.Permission;
import by.salary.serviceinvitation.filters.PermissionsEnum;
import by.salary.serviceinvitation.model.InvitationRequestDTO;
import by.salary.serviceinvitation.model.InvitationResponseDTO;
import by.salary.serviceinvitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private InvitationRepository invitationRepository;


    private WebClient.Builder webClientBuilder;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository,
                             WebClient.Builder webClientBuilder) {
        this.invitationRepository = invitationRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<InvitationResponseDTO> getAllInvitations() {
        List<InvitationResponseDTO> list = new ArrayList<>();
        invitationRepository.findAll().forEach(invitation -> list.add(new InvitationResponseDTO(invitation)));
        return list;
    }

    public InvitationResponseDTO getInvitationById(BigInteger id) {
        return new InvitationResponseDTO(invitationRepository.findById(id).get());
    }



    private String generateInvitationCode(){
        while (true){
            String code = String.valueOf((int) (Math.random() * 1000000000));
            if (!invitationRepository.existsByInvitationCode(code)){
                return code;
            }
        }
    }

    public InvitationResponseDTO createInvitation(InvitationRequestDTO invitationRequestDTO, String directorEmail) {
        if(!isPermitted(directorEmail)){
            throw new NotEnoughtPermissionsException("User with email " + directorEmail + " has not enough permissions", HttpStatus.FORBIDDEN);
        }
        BigInteger organisationId = getOrganisationId(directorEmail);
        Invitation invitation = new Invitation(invitationRequestDTO, generateInvitationCode(), organisationId);
        return new InvitationResponseDTO(invitationRepository.save(invitation));
    }

    private BigInteger getOrganisationId(String directorEmail) {
        return new BigInteger(Objects.requireNonNull(webClientBuilder
                .build()
                .post()
                .uri("http://service-user:8080/users/getorganisationid/" + directorEmail)
                .retrieve()
                .bodyToMono(String.class)
                .block()));
    }


    private Boolean isPermitted(String directorEmail) {
        return Objects.requireNonNull(webClientBuilder
                .build()
                .get()
                .uri("http://service-user:8080/users/ispermitted/" + directorEmail)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }

    public InvitationResponseDTO deleteInvitation(String invitationCode) {
        Optional<Invitation> invitation = invitationRepository.findByInvitationCode(invitationCode);
        if(invitation.isEmpty()){
            throw new InvitationNotFoundException("Invitation not found", HttpStatus.NOT_FOUND);
        }
        invitationRepository.deleteById(invitation.get().getId());
        return new InvitationResponseDTO();
    }

    public String getInvitation(String invitationCode) {
        Optional<Invitation> invitation = invitationRepository.findByInvitationCode(invitationCode);
        if(invitation.isEmpty()){
            throw new InvitationNotFoundException("Invitation not found", HttpStatus.NOT_FOUND);
        }
        return invitation.get().toString();
    }
}
