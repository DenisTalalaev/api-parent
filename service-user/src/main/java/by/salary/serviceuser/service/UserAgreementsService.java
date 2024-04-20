package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.entities.PermissionsEnum;
import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.entities.UserAgreement;
import by.salary.serviceuser.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.UserAgreementRequestDTO;
import by.salary.serviceuser.model.UserAgreementResponseDTO;
import by.salary.serviceuser.repository.UserAgreementsRepository;
import by.salary.serviceuser.repository.UserRepository;
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
public class UserAgreementsService {

    private final UserAgreementsRepository userAgreementsRepository;
    private final UserRepository userRepository;

    private WebClient.Builder webClientBuilder;

    @Autowired
    public UserAgreementsService(UserAgreementsRepository userAgreementsRepository, UserRepository userRepository, WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.userAgreementsRepository = userAgreementsRepository;
        this.userRepository = userRepository;
    }


    public List<UserAgreementResponseDTO> getAllUserAgreements(BigInteger userId, String email) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }

        if (!userId.equals(userRepository.findByUserEmail(email).get().getId())
                & !Permission.isPermitted(userRepository.findByUserEmail(email).get(), PermissionsEnum.CRUD_USER_AGREEMENT)
        ) {
            throw new NotEnoughtPermissionsException("User with id " + userId + " not found", HttpStatus.FORBIDDEN);
        }

        List<UserAgreementResponseDTO> userAgreements = new ArrayList<>();
        userAgreementsRepository.findAll().forEach(userAgreement -> {
            if (userAgreement.getUser().getId().equals(userId)) {
                userAgreements.add(new UserAgreementResponseDTO(userAgreement));
            }
        });


        return userAgreements;
    }

    public void deleteUserAgreement(BigInteger agreementId, String email) {

        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }

        if (!Permission.isPermitted(optUser.get(), PermissionsEnum.CRUD_USER_AGREEMENT)) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if (!userRepository.existsByUserEmail(email)) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if (!userAgreementsRepository.existsById(agreementId)) {
            throw new UserNotFoundException("User agreement with id " + agreementId + " not found", HttpStatus.NOT_FOUND);
        }
        userAgreementsRepository.deleteById(agreementId);
    }


    public UserAgreementResponseDTO createUserAgreement(UserAgreementRequestDTO userAgreementRequestDTO, BigInteger userId, String email) {
        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if (!Permission.isPermitted(optUser.get(), PermissionsEnum.CRUD_USER_AGREEMENT)) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        UserAgreement userAgreement = new UserAgreement(
                userAgreementRequestDTO,
                userRepository.findById(userId).get(),
                optUser.get(),
                getAgreementState(userAgreementRequestDTO.getAgreementId()));
        return new UserAgreementResponseDTO(userAgreementsRepository.save(userAgreement));
    }

    private String getAgreementState(BigInteger id) {
        return Objects.requireNonNull(webClientBuilder
                .build()
                .get()
                .uri("http://service-agreement:8080/agreements/getagreementstate/" + id)
                .retrieve()
                .bodyToMono(String.class)
                .block());

    }

}
