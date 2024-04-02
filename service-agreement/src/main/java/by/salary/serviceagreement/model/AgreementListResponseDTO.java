package by.salary.serviceagreement.model;

import by.salary.serviceagreement.entities.AgreementList;
import by.salary.serviceagreement.entities.AgreementState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class AgreementListResponseDTO {

    private BigInteger id;
    private String stateListName;
    private List<AgreementStateResponseDTO> statesList;

    public AgreementListResponseDTO(AgreementList agreementList) {
        this.id = agreementList.getId();
        this.stateListName = agreementList.getListName();
        this.statesList = new ArrayList<>();
        for (AgreementState agreementState : agreementList.getAgreementListStates()) {
            this.statesList.add(new AgreementStateResponseDTO(agreementState));
        }
    }
}
