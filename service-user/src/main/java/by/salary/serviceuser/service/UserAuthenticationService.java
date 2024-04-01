package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Invitation;
import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import by.salary.serviceuser.model.*;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private UserRepository userRepository;
    private OrganisationRepository organisationRepository;

    WebClient.Builder webClientBuilder;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository, WebClient.Builder webClientBuilder, OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
        this.webClientBuilder = webClientBuilder;
    }


    public UserAuthenticationResponseDTO auth(UserAuthenticationRequestDTO userAuthenticationRequestDTO) {
        if (userAuthenticationRequestDTO.getAuthenticationRegistrationId().equals(AuthenticationRegistrationId.local)) {
            if (userAuthenticationRequestDTO.getUserEmail() != null) {
                return new UserAuthenticationResponseDTO(userRepository.findByUserEmail(userAuthenticationRequestDTO.getUserEmail()));
            } else if (userAuthenticationRequestDTO.getUsername() != null) {
                return new UserAuthenticationResponseDTO(userRepository.findByUsername(userAuthenticationRequestDTO.getUsername()));
            }
            throw new RuntimeException();
        }
        return new UserAuthenticationResponseDTO(
                userRepository.findByUserAuthorisationAttributeKey(userAuthenticationRequestDTO.getAuthenticationRegistrationId())
        );
    }


    public UserRegistrationResponseDTO reg(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        if (userRepository.findByUserEmail(userRegistrationRequestDTO.getUserEmail()).isPresent()
                || userRepository.findByUsername(userRegistrationRequestDTO.getUsername()).isPresent()
        ) {
            return new UserRegistrationResponseDTO(HttpStatus.CONFLICT, "User already registered");
        }
        User user = new User();
        user.setAuthenticationRegistrationId(userRegistrationRequestDTO.getAuthenticationRegistrationId());
        if(userRegistrationRequestDTO.getAuthenticationRegistrationId().equals(AuthenticationRegistrationId.local)) {
            user.setUsername(userRegistrationRequestDTO.getUsername());
            user.setUserEmail(userRegistrationRequestDTO.getUserEmail());
            user.setUserPassword(userRegistrationRequestDTO.getUserPassword());
            userRepository.save(user);
            return new UserRegistrationResponseDTO(user);
        }
        user.setAuthenticationRegistrationId(userRegistrationRequestDTO.getAuthenticationRegistrationId());
        user.setUserEmail(userRegistrationRequestDTO.getUserEmail());
        user.setUsername(userRegistrationRequestDTO.getUserEmail());
        user.setImageURI(userRegistrationRequestDTO.getPictureUri());
        userRepository.save(user);
        return new UserRegistrationResponseDTO(user);
    }

    private void deleteInvitation(String invitationCode) {
        webClientBuilder.build()
                .delete()
                .uri("lb://service-invitation/invitations/" + invitationCode)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    public UserJoinOrganisationResponseDTO joinOrganisation(UserJoinOrganisationRequestDTO userJoinOrganisationRequestDTO, String email) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if(userOpt.isEmpty()){
            throw new RuntimeException("No user found");
        }
        User user = userOpt.get();
        Invitation invitation = getInvitation(userJoinOrganisationRequestDTO.getInvitationCode());

        user.setOrganisation(organisationRepository.findById(invitation.getOrganisationId()).get());
        user.setUserFirstName(invitation.getUserFirstName());
        user.setUserSurname(invitation.getUserSurname());
        user.setUserSecondName(invitation.getUserSecondName());


        userRepository.save(user);
        deleteInvitation(userJoinOrganisationRequestDTO.getInvitationCode());
        return new UserJoinOrganisationResponseDTO(user);

    }

    private Invitation getInvitation(String invitationCode) {
        return new Invitation(webClientBuilder.build()
                .get()
                .uri("lb://service-invitation/invitations/" + invitationCode)
                .retrieve()
                .bodyToMono(String.class)
                .block());
    }
}