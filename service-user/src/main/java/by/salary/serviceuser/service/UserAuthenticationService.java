package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.*;
import by.salary.serviceuser.exceptions.OrganisationNotFoundException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import by.salary.serviceuser.model.user.authentication.UserAuthenticationRequestDTO;
import by.salary.serviceuser.model.user.authentication.UserAuthenticationResponseDTO;
import by.salary.serviceuser.model.user.organisation.UserJoinOrganisationRequestDTO;
import by.salary.serviceuser.model.user.organisation.UserJoinOrganisationResponseDTO;
import by.salary.serviceuser.model.user.registration.UserRegistrationRequestDTO;
import by.salary.serviceuser.model.user.registration.UserRegistrationResponseDTO;
import by.salary.serviceuser.repository.AuthorityRepository;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.Optional;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private UserRepository userRepository;
    private OrganisationRepository organisationRepository;
    private AuthorityRepository authorityRepository;

    WebClient.Builder webClientBuilder;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository, WebClient.Builder webClientBuilder, OrganisationRepository organisationRepository, AuthorityRepository authorityRepository) {
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
        this.webClientBuilder = webClientBuilder;
        this.authorityRepository = authorityRepository;
    }


    public UserAuthenticationResponseDTO auth(UserAuthenticationRequestDTO userAuthenticationRequestDTO) {
        if (userAuthenticationRequestDTO.getAuthenticationRegistrationId().equals(AuthenticationRegistrationId.local)) {
            if (userAuthenticationRequestDTO.getUserEmail() != null) {
                Optional<User> optUser = userRepository.findByUserEmail(userAuthenticationRequestDTO.getUserEmail());
                if (optUser.isEmpty()) {
                    throw new UserNotFoundException("User with email " + userAuthenticationRequestDTO.getUserEmail() + " not found", HttpStatus.NOT_FOUND);
                }
                return new UserAuthenticationResponseDTO(optUser);
            } else if (userAuthenticationRequestDTO.getUsername() != null) {
                Optional<User> optUser = userRepository.findByUsername(userAuthenticationRequestDTO.getUsername());
                if (optUser.isEmpty()) {
                    throw new UserNotFoundException("User with username " + userAuthenticationRequestDTO.getUsername() + " not found", HttpStatus.NOT_FOUND);
                }
                return new UserAuthenticationResponseDTO(optUser);
            }
            throw new UserNotFoundException("User not found", HttpStatus.NOT_FOUND);
        }
        return new UserAuthenticationResponseDTO(
                userRepository.findByUserAuthorisationAttributeKey(userAuthenticationRequestDTO.getAuthorizationRegistrationKey())
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
            user.setIs2FEnabled(userRegistrationRequestDTO.getIs2FEnabled());
            if(user.getIs2FEnabled() == null){
                user.setIs2FEnabled(false);
            }
            user.setIs2FVerified(false);
            userRepository.save(user);
            return new UserRegistrationResponseDTO(user);
        }
        user.setAuthenticationRegistrationId(userRegistrationRequestDTO.getAuthenticationRegistrationId());
        user.setUserEmail(userRegistrationRequestDTO.getUserEmail());
        user.setUsername(userRegistrationRequestDTO.getUserEmail());
        user.setImageURI(userRegistrationRequestDTO.getPictureUri());
        user.setUserAuthorisationAttributeKey(userRegistrationRequestDTO.getAuthenticationRegistrationKey());
        user.setIs2FEnabled(true);
        user.setIs2FVerified(true);
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
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        Invitation invitation = getInvitation(userJoinOrganisationRequestDTO.getInvitationCode());
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(invitation.getOrganisationId());
        if(optionalOrganisation.isEmpty()){
            throw new OrganisationNotFoundException("Organisation with id " + invitation.getOrganisationId() + " not found", HttpStatus.NOT_FOUND);
        }
        user.setOrganisation(optionalOrganisation.get());
        user.setUserFirstName(invitation.getUserFirstName());
        user.setUserSurname(invitation.getUserSurname());
        user.setUserSecondName(invitation.getUserSecondName());

        user.getAuthorities().add(authorityRepository.findByAuthority(new Authority("USER").getAuthority()).get());

        userRepository.save(user);
        deleteInvitation(userJoinOrganisationRequestDTO.getInvitationCode());
        return new UserJoinOrganisationResponseDTO(user);

    }

    private Invitation getInvitation(String invitationCode) {
        return new Invitation(Objects.requireNonNull(webClientBuilder.build()
                .get()
                .uri("lb://service-invitation/invitations/" + invitationCode)
                .retrieve()
                .bodyToMono(String.class)
                .block()));
    }
}