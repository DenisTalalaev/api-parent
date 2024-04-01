package by.salary.serviceinvitation.service;

import by.salary.serviceinvitation.entities.Invitation;
import by.salary.serviceinvitation.model.InvitationRequestDTO;
import by.salary.serviceinvitation.model.InvitationResponseDTO;
import by.salary.serviceinvitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
            String code = String.valueOf((int) (Math.random() * 1000000));
            if (!invitationRepository.existsByInvitationCode(code)){
                return code;
            }
        }
    }

    public InvitationResponseDTO createInvitation(InvitationRequestDTO invitationRequestDTO) {
        Invitation invitation = new Invitation(invitationRequestDTO, generateInvitationCode());
        return new InvitationResponseDTO(invitationRepository.save(invitation));
    }

    public InvitationResponseDTO deleteInvitation(String invitationCode) {
        BigInteger id = invitationRepository.findByInvitationCode(invitationCode).get().getId();
        invitationRepository.deleteById(id);
        return new InvitationResponseDTO();
    }

    public String getInvitation(String invitationCode) {
        return invitationRepository.findByInvitationCode(invitationCode).get().toString();
    }
}
