package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import by.salary.serviceuser.model.UserAuthenticationRequestDTO;
import by.salary.serviceuser.model.UserAuthenticationResponseDTO;
import by.salary.serviceuser.model.UserRegistrationRequestDTO;
import by.salary.serviceuser.model.UserRegistrationResponseDTO;
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

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private UserRepository userRepository;

    WebClient.Builder webClientBuilder;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository, WebClient.Builder webClientBuilder) {
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

    public String getUserIdByInvitation(String invitation) {
        return webClientBuilder.build()
                .get()
                .uri("lb://service-invitation/invitations/getUser/" + invitation)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    public UserRegistrationResponseDTO reg(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        if (userRepository.findByUserEmail(userRegistrationRequestDTO.getUserEmail()).isPresent()
                || userRepository.findByUsername(userRegistrationRequestDTO.getUsername()).isPresent()
        ) {
            return new UserRegistrationResponseDTO(HttpStatus.CONFLICT, "User already registered");
        }
        String invitationCodeFindQuery = getUserIdByInvitation(userRegistrationRequestDTO.getInvitationCode());
        if(invitationCodeFindQuery.equals("Invalid invitation code")) {
            return new UserRegistrationResponseDTO(HttpStatus.NO_CONTENT, "Invalid invitation code");
        }
        BigInteger userId =new BigInteger(invitationCodeFindQuery);
        System.out.println(userId);
        User user = userRepository.findById(userId).get();
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
        deleteInvitation(userRegistrationRequestDTO.getInvitationCode());
        return new UserRegistrationResponseDTO(user);
    }

    private void deleteInvitation(String invitationCode) {
        webClientBuilder.build()
                .delete()
                .uri("lb://service-invitation/invitations/" + invitationCode)
                .retrieve()
                .bodyToMono(String.class).block();
    }
}