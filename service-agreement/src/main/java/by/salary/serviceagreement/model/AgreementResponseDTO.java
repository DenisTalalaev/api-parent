package by.salary.serviceagreement.model;

import by.salary.serviceagreement.entities.Agreement;
import by.salary.serviceagreement.entities.AgreementList;
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
public class AgreementResponseDTO {

    private BigInteger id;

    private List<AgreementListResponseDTO> agreementLists;

    public AgreementResponseDTO(Agreement agreement) {
        this.id = agreement.getId();
        this.agreementLists = new ArrayList<>();
        for (AgreementList agreementList : agreement.getAgreementLists()) {
            this.agreementLists.add(new AgreementListResponseDTO(agreementList));
        }
    }
}
