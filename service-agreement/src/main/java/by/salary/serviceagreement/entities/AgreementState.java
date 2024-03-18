package by.salary.serviceagreement.entities;


/*
The row in the table of agreement states
Example
10.2.1 For high achievements in the field of It
10.2.1 - stateName
For high achievements in the field of It - stateInfo
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementState {
    private BigInteger id;
    private String stateName;
    private String stateInfo;

}
