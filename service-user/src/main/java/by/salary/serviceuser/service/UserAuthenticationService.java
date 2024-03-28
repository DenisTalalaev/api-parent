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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.Objects;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private UserRepository userRepository;

    WebClient.Builder webClientBuilder;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public BigInteger getUserIdByInvitation(String invitationCode) {
        return webClientBuilder.build()
                .get()
                .uri("http://service-invitation/invitations/getuser/" + invitationCode, 1)
                .retrieve()
                .bodyToMono(BigInteger.class)
                .block();
    }

    public UserRegistrationResponseDTO reg(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        if (userRepository.findByUserEmail(userRegistrationRequestDTO.getUserEmail()).isPresent()
                || userRepository.findByUsername(userRegistrationRequestDTO.getUsername()).isPresent()
        ) {
            return new UserRegistrationResponseDTO(HttpStatus.CONFLICT, "User already registered");
        }
        BigInteger userId = getUserIdByInvitation(userRegistrationRequestDTO.getInvitationCode());
        if(userId != null) {
            return new UserRegistrationResponseDTO(HttpStatus.NO_CONTENT, "Invalid invitation code");
        }
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
        return new UserRegistrationResponseDTO(user);
    }
}