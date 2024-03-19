package by.salary.serviceagreement.entities;


/**
The row in the table of agreement states
Example
10.2.1 For high achievements in the field of It
10.2.1 - stateName
For high achievements in the field of It - stateInfo
 */

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class AgreementState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;
    @Column(unique = true)
    private String stateName;
    private String stateInfo;

}
