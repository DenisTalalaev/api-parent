package by.salary.serviceagreement.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
Container of agreement document for one organisation
 */

import java.math.BigInteger;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agreement {

    private BigInteger id;
    private ArrayList<AgreementStatesList> agreementStatesList;
}
