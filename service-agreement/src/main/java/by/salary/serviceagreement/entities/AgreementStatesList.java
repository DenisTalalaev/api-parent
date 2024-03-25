package by.salary.serviceagreement.entities;

import by.salary.serviceagreement.model.AgreementStateListRequestDTO;
import by.salary.serviceagreement.model.AgreementStateResponseDTO;
import jakarta.persistence.*;
import lombok.*;


/**
The list of agreement states
Example
For high achievements
state 1(10.2.1 For high achievements in the field of It)
state 2(10.2.2 For high achievements in the field of Internet)
state 2(10.2.3 For high achievements in the field of other)
 */
import java.awt.geom.AffineTransform;
import java.math.BigInteger;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class AgreementStatesList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;
    @Column(unique = true)
    private String stateListName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "agreementStatesList")
    private ArrayList<AgreementState> statesList;

    @ManyToOne(fetch = FetchType.EAGER)
    private Agreement agreement;

    public AgreementStatesList(AgreementStateListRequestDTO agreementStateListRequestDTO) {
        this.stateListName = agreementStateListRequestDTO.getStateListName();
        this.statesList = agreementStateListRequestDTO.getStatesList();
    }

    public void addState(AgreementState state){
        statesList.add(state);
    }
}
