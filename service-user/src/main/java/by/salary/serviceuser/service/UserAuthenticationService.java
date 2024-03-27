package by.salary.serviceuser.service;

import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import by.salary.serviceuser.model.UserAuthenticationRequestDTO;
import by.salary.serviceuser.model.UserAuthenticationResponseDTO;
import by.salary.serviceuser.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

    private UserRepository userRepository;

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
}