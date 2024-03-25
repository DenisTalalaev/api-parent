package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.UserAgreement;
import by.salary.serviceuser.model.UserAgreementRequestDTO;
import by.salary.serviceuser.model.UserAgreementResponseDTO;
import by.salary.serviceuser.repository.UserAgreementsRepository;
import by.salary.serviceuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<UserAgreementResponseDTO> getAllUserAgreements(BigInteger userId) {
        List<UserAgreementResponseDTO> userAgreements = new ArrayList<>();
        userAgreementsRepository.findAll().forEach(userAgreement -> {
            if (userAgreement.getUser().getId().equals(userId)) {
                userAgreements.add(new UserAgreementResponseDTO(userAgreement));
            }
        });
        return userAgreements;
    }

    public void deleteUserAgreement(BigInteger agreementId) {
        userAgreementsRepository.deleteById(agreementId);
    }

    public void deleteUserAgreements(BigInteger userId) {
        userAgreementsRepository.findAll().forEach(
                userAgreement -> {
                    if (userAgreement.getUser().getId().equals(userId)) {
                        userAgreementsRepository.deleteById(userAgreement.getId());
                    }
                }
        );
    }

    public UserAgreementResponseDTO createUserAgreement(UserAgreementRequestDTO userAgreementRequestDTO, BigInteger userId) {
        UserAgreement userAgreement = new UserAgreement(userAgreementRequestDTO, userRepository.findById(userId).get());
        return new UserAgreementResponseDTO(userAgreementsRepository.save(userAgreement));
    }
}
