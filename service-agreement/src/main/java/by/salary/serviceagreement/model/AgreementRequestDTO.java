package by.salary.serviceagreement.model;

import by.salary.serviceagreement.entities.AgreementStatesList;
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
public class AgreementRequestDTO {
    private BigInteger id;
    private ArrayList<AgreementStatesList> agreementStatesList;
}
