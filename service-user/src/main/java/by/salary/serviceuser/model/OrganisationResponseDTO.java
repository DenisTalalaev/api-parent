package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.Organisation;
import by.salary.serviceuser.entities.User;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganisationResponseDTO {

    private BigInteger id;
    private String organisationName;
    private String organisationAddress;

    private String organisationContactNumber;
    private BigInteger directorId;

    private BigDecimal baseReward;

    private BigInteger agreementId;

    public OrganisationResponseDTO(Organisation organisation) {

        this.id = organisation.getId();
        this.organisationName = organisation.getOrganisationName();
        this.organisationAddress = organisation.getOrganisationAddress();
        this.organisationContactNumber = organisation.getOrganisationContactNumber();
        this.directorId = organisation.getDirector().getId();
        this.baseReward = organisation.getBaseReward();
        this.agreementId = organisation.getAgreementId();
    }
}
