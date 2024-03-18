package by.salary.serviceuser.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;


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


}
