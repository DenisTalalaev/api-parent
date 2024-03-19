package by.salary.serviceuser.entities;


import by.salary.serviceuser.model.OrganisationRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
Organisation
One table with all organisations
Organisation=
    organisationName
    organisationAddress
    organisationContactNumber
    organisationDirectorId - PRE created user id.
    baseReward - BASE reward (usually from base value, ~209 BYN, set by admin)
    agreementId - organisations agreement ID
Director = user with * permissions. Created in time of creation of the organisation

 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @Size(min = 2, max = 100)
    @Column(unique = true)
    private String organisationName;

    @Size(min = 2, max = 200)
    private String organisationAddress;

    private String organisationContactNumber;

    @NotNull
    private BigInteger organisationDirectorId;

    private BigDecimal baseReward;

    private BigInteger agreementId;

    public Organisation(OrganisationRequestDTO organisationRequestDTO) {

        this.organisationName = organisationRequestDTO.getOrganisationName();
        this.organisationAddress = organisationRequestDTO.getOrganisationAddress();
        this.organisationContactNumber = organisationRequestDTO.getOrganisationContactNumber();
        this.organisationDirectorId = organisationRequestDTO.getOrganisationDirectorId();
        this.baseReward = organisationRequestDTO.getBaseReward();
        this.agreementId = organisationRequestDTO.getAgreementId();
    }

    public void update(OrganisationRequestDTO organisationRequestDTO) {

        this.organisationName = organisationRequestDTO.getOrganisationName() == null? this.organisationName : organisationRequestDTO.getOrganisationName();
        this.organisationAddress = organisationRequestDTO.getOrganisationAddress() ==null? this.organisationAddress : organisationRequestDTO.getOrganisationAddress();
        this.organisationContactNumber = organisationRequestDTO.getOrganisationContactNumber() == null? this.organisationContactNumber : organisationRequestDTO.getOrganisationContactNumber();
        this.organisationDirectorId = organisationRequestDTO.getOrganisationDirectorId() == null? this.organisationDirectorId : organisationRequestDTO.getOrganisationDirectorId();
        this.baseReward = organisationRequestDTO.getBaseReward() == null? this.baseReward : organisationRequestDTO.getBaseReward();
        this.agreementId = organisationRequestDTO.getAgreementId() == null? this.agreementId : organisationRequestDTO.getAgreementId();
    }
}
