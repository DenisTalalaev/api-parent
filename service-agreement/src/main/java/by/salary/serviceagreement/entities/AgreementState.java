package by.salary.serviceagreement.entities;

import by.salary.serviceagreement.model.AgreementStateRequestDTO;
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

    private String stateName;
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
}