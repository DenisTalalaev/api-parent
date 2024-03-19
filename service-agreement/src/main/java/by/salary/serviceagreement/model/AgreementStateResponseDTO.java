package by.salary.serviceagreement.model;



import by.salary.serviceagreement.entities.AgreementState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AgreementStateResponseDTO {

    private BigInteger id;
    private String stateName;
    private String stateInfo;

    public AgreementStateResponseDTO(AgreementState agreementState) {
        this.id = agreementState.getId();
        this.stateName = agreementState.getStateName();
        this.stateInfo = agreementState.getStateInfo();
    }
}
