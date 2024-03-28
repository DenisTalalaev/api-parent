package by.salary.serviceinvitation.service;

import by.salary.serviceinvitation.entities.Invitation;
import by.salary.serviceinvitation.interfaces.UserRequestDTO;
import by.salary.serviceinvitation.model.InvitationRequestDTO;
import by.salary.serviceinvitation.model.InvitationResponseDTO;
import by.salary.serviceinvitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private InvitationRepository invitationRepository;


    private WebClient.Builder webClientBuilder;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository, WebClient.Builder webClientBuilder) {
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

    public Optional<BigInteger> createUser(String userFirstName, String userSecondName, String userSurnameName, BigInteger organisationId) {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                userFirstName,
                userSecondName,
                userSurnameName,
                true,
                true,
                true,
                false,
                organisationId
        );
        return webClientBuilder.build()
                .post()
                .uri("lb://service-user/users")
                .body(userRequestDTO, UserRequestDTO.class)
                .retrieve()
                .bodyToMono(BigInteger.class).blockOptional();
    }
    public InvitationResponseDTO createInvitation(InvitationRequestDTO invitationRequestDTO) {
        Optional<BigInteger> userId = createUser(invitationRequestDTO.getUserFirstName(), invitationRequestDTO.getUserSecondName(), invitationRequestDTO.getUserSurnameName(), invitationRequestDTO.getOrganisationId());
        Invitation invitation = new Invitation(invitationRequestDTO, userId.get(), generateInvitationCode());
        return new InvitationResponseDTO(invitationRepository.save(invitation));
    }

    public InvitationResponseDTO deleteInvitation(BigInteger id) {
        invitationRepository.deleteById(id);
        return new InvitationResponseDTO();
    }

    public BigInteger getUserByInvitationCode(String userInvitationCode) {
        if (!invitationRepository.existsByInvitationCode(userInvitationCode)) {
            throw new RuntimeException("Invitation not found");
        }
        return new BigInteger(invitationRepository.findByInvitationCode(userInvitationCode).get().getInvitationCode());
    }
}
