package by.salary.serviceagreement.entities;

import jakarta.persistence.*;
import lombok.*;

/**
Container of agreement document for one organisation
 */

import java.math.BigInteger;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @OneToMany(fetch = FetchType.EAGER)
    private ArrayList<AgreementStatesList> agreementStatesList;
}
