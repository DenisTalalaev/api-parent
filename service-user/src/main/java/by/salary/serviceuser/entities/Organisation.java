package by.salary.serviceuser.entities;


import by.salary.serviceuser.model.OrganisationRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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
    private String organisationName;

    @Size(min = 2, max = 200)
    private String organisationAddress;

    private String organisationContactNumber;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User director;

    private BigDecimal baseReward;

    private BigInteger agreementId;

    @OneToMany
    @JoinColumn(name = "organisation_id")
    private List<User> users;

    public Organisation(OrganisationRequestDTO organisationRequestDTO) {
        this.organisationName = organisationRequestDTO.getOrganisationName();
        this.organisationAddress = organisationRequestDTO.getOrganisationAddress();
        this.organisationContactNumber = organisationRequestDTO.getOrganisationContactNumber();
        this.baseReward = organisationRequestDTO.getBaseReward();
        this.director = null;
    }


    public void update(OrganisationRequestDTO organisationRequestDTO) {
        this.organisationName = organisationRequestDTO.getOrganisationName() == null? this.organisationName : organisationRequestDTO.getOrganisationName();
        this.organisationAddress = organisationRequestDTO.getOrganisationAddress() ==null? this.organisationAddress : organisationRequestDTO.getOrganisationAddress();
        this.organisationContactNumber = organisationRequestDTO.getOrganisationContactNumber() == null? this.organisationContactNumber : organisationRequestDTO.getOrganisationContactNumber();
        this.baseReward = organisationRequestDTO.getBaseReward() == null? this.baseReward : organisationRequestDTO.getBaseReward();
    }
}
