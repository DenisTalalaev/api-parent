package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.Organisation;
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
    private BigInteger organisationDirectorId;

    private BigDecimal baseReward;

    private BigInteger agreementId;

    public OrganisationResponseDTO(Organisation organisation) {

        this.id = organisation.getId();
        this.organisationName = organisation.getOrganisationName();
        this.organisationAddress = organisation.getOrganisationAddress();
        this.organisationContactNumber = organisation.getOrganisationContactNumber();
        this.organisationDirectorId = organisation.getOrganisationDirectorId();
        this.baseReward = organisation.getBaseReward();
        this.agreementId = organisation.getAgreementId();
    }
}
