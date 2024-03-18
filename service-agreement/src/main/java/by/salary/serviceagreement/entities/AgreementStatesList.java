package by.salary.serviceagreement.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
The list of agreement states
Example
For high achievements
state 1(10.2.1 For high achievements in the field of It)
state 2(10.2.2 For high achievements in the field of Internet)
state 2(10.2.3 For high achievements in the field of other)
 */
import java.math.BigInteger;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementStatesList {

    private BigInteger id;
    private String stateListName;
    private ArrayList<AgreementState> statesList;

    public void addState(AgreementState state){
        statesList.add(state);
    }
}
