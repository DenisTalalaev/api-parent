package by.salary.serviceagreement.entities;


/**
The row in the table of agreement states
Example
10.2.1 For high achievements in the field of It
10.2.1 - stateName
For high achievements in the field of It - stateInfo
 */

import by.salary.serviceagreement.model.AgreementStateRequestDTO;
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

    @ManyToOne(fetch = FetchType.EAGER)
    private AgreementStatesList agreementStatesList;

    public AgreementState(AgreementStateRequestDTO agreementStateRequestDTO) {
        this.stateName = agreementStateRequestDTO.getStateName();
        this.stateInfo = agreementStateRequestDTO.getStateInfo();
    }
}
