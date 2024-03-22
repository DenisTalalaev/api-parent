package by.salary.serviceuseragreement.service;

import by.salary.serviceuseragreement.entities.UserAgreement;
import by.salary.serviceuseragreement.model.UserAgreementRequestDTO;
import by.salary.serviceuseragreement.model.UserAgreementResponseDTO;
import by.salary.serviceuseragreement.repository.UserAgreementRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Service
public class UserAgreementService {
    private UserAgreementRepository userAgreementRepository;

    @Autowired
    public UserAgreementService(UserAgreementRepository userAgreementRepository){
        this.userAgreementRepository = userAgreementRepository;

    }


    public List<UserAgreementResponseDTO> getAllUserAgreements() {
        List<UserAgreementResponseDTO> list = new ArrayList<>();
        userAgreementRepository.findAll().forEach(userAgreement -> list.add(new UserAgreementResponseDTO(userAgreement)));
        return list;
    }

    public UserAgreementResponseDTO getUserAgreementById(BigInteger id) {
        return new UserAgreementResponseDTO(userAgreementRepository.findById(id).get());
    }


    public UserAgreementResponseDTO createUserAgreement(UserAgreementRequestDTO userAgreementRequestDTO) {
        return new UserAgreementResponseDTO(userAgreementRepository.save(new UserAgreement(userAgreementRequestDTO)));
    }

    public UserAgreementResponseDTO updateUserAgreement(UserAgreementRequestDTO userAgreementRequestDTO) {
        UserAgreement userAgreement = userAgreementRepository.findById(userAgreementRequestDTO.getId()).get();
        if (!userAgreementRepository.existsById(userAgreementRequestDTO.getId())) {
            throw new RuntimeException("UserAgreement not found");
        }
        userAgreement.update(userAgreementRequestDTO);
        return new UserAgreementResponseDTO(userAgreementRepository.save(userAgreement));
    }

    public void deleteUserAgreement(BigInteger id) {
        userAgreementRepository.deleteById(id);
    }
}
