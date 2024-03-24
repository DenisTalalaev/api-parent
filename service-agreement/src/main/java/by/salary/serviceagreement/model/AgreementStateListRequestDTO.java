package by.salary.serviceagreement.model;

import by.salary.serviceagreement.entities.AgreementState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class AgreementStateListRequestDTO {

    private BigInteger id;
    private String stateListName;
    private ArrayList<AgreementState> statesList;

}
