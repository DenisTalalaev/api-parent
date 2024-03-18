package by.salary.serviceagreement.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementStatesList {

    private BigInteger id;
    private String StateListName;
    private ArrayList<AgreementState> StatesList;

    public void addState(AgreementState state){
        StatesList.add(state);
    }
}
