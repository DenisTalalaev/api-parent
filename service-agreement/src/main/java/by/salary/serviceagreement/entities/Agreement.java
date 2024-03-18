package by.salary.serviceagreement.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agreement {

    private Long id;
    private ArrayList<AgreementStatesList> AgreementStatesList;
}
