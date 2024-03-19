package by.salary.serviceagreement.model;

import by.salary.serviceagreement.entities.Agreement;
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
public class AgreementResponseDTO {

    private BigInteger id;

    private ArrayList<AgreementStatesList> agreementStatesList;

    public AgreementResponseDTO(Agreement agreement) {
        this.id = agreement.getId();
        this.agreementStatesList = agreement.getAgreementStatesList();
    }
}
