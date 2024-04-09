package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.entities.PermissionsEnum;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAgreementsService {

    private final UserAgreementsRepository userAgreementsRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserAgreementsService(UserAgreementsRepository userAgreementsRepository, UserRepository userRepository) {
        this.userAgreementsRepository = userAgreementsRepository;
        this.userRepository = userRepository;
    }


    public List<UserAgreementResponseDTO> getAllUserAgreements(BigInteger userId, String email, List<Permission> permissions) {
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }

        if(!userId.equals(userRepository.findByUserEmail(email).get().getId())
            & ! permissions.contains(new Permission(PermissionsEnum.READ_USER_AGREEMENT))
        ){
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

    public void deleteUserAgreement(BigInteger agreementId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.DELETE_USER_AGREEMENT))){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if(!userRepository.existsByUserEmail(email)){
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if(!userAgreementsRepository.existsById(agreementId)){
            throw new UserNotFoundException("User agreement with id " + agreementId + " not found", HttpStatus.NOT_FOUND);
        }
        userAgreementsRepository.deleteById(agreementId);
    }

    public void deleteUserAgreements(BigInteger userId, String email, List<Permission> permissions) {
        if(!permissions.contains(new Permission(PermissionsEnum.DELETE_USER_AGREEMENT))){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if(!userRepository.existsByUserEmail(email)){
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        userAgreementsRepository.findAll().forEach(
                userAgreement -> {
                    if (userAgreement.getUser().getId().equals(userId)) {
                        userAgreementsRepository.deleteById(userAgreement.getId());
                    }
                }
        );
    }

    public UserAgreementResponseDTO createUserAgreement(UserAgreementRequestDTO userAgreementRequestDTO, BigInteger userId, String email, List<Permission> permissions) {
        if(!userRepository.existsByUserEmail(email)){
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if(!permissions.contains(new Permission(PermissionsEnum.ADD_USER_AGREEMENT))){
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }

        UserAgreement userAgreement = new UserAgreement(userAgreementRequestDTO, userRepository.findById(userId).get());
        return new UserAgreementResponseDTO(userAgreementsRepository.save(userAgreement));
    }
}
