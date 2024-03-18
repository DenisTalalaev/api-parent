package by.salary.serviceagreement.entities;


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
    private String StateName;
    private String StateInfo;

}
