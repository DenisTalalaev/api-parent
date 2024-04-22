package by.salary.serviceagreement.entities;

import by.salary.serviceagreement.model.AgreementStateRequestDTO;
import by.salary.serviceagreement.util.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgreementState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @ManyToOne
    private AgreementList agreementLists;

    @Convert(converter = AttributeEncryptor.class)
    private String stateName;
    @Convert(converter = AttributeEncryptor.class)
    private String stateInfo;

    public AgreementState(String stateName, String stateInfo, AgreementList agreementList) {
        this.stateName = stateName;
        this.stateInfo = stateInfo;
        this.agreementLists = agreementList;
    }

    public AgreementState(AgreementStateRequestDTO agreementStateListRequestDTO, AgreementList agreementList) {
        this.stateName = agreementStateListRequestDTO.getStateName();
        this.stateInfo = agreementStateListRequestDTO.getStateInfo();
        this.agreementLists = agreementList;
    }


    public AgreementState update(AgreementStateRequestDTO agreementStateListRequestDTO) {
        this.stateName = agreementStateListRequestDTO.getStateName();
        this.stateInfo = agreementStateListRequestDTO.getStateInfo();
        return this;
    }
}