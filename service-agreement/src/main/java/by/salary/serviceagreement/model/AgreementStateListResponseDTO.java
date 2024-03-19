package by.salary.serviceagreement.model;

import by.salary.serviceagreement.entities.AgreementState;
import by.salary.serviceagreement.entities.AgreementStatesList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class AgreementStateListResponseDTO {

    private BigInteger id;
    private String stateListName;
    private ArrayList<AgreementState> statesList;

    public AgreementStateListResponseDTO(AgreementStatesList agreementStatesList) {
        this.id = agreementStatesList.getId();
        this.stateListName = agreementStatesList.getStateListName();
        this.statesList = agreementStatesList.getStatesList();
    }
}
