package by.salary.serviceuser.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
Director = user with * permissions. Created in time of creation of the organisation

 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {

    private BigInteger id;
    private String organisationName;
    private String organisationAddress;
    private String organisationContactNumber;
    private BigInteger organisationDirectorId;
    private BigDecimal baseReward;

}
