package by.salary.serviceagreement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AgreementList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @ManyToOne
    private Agreement agreement;

    @OneToMany(mappedBy = "agreementLists", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgreementState> agreementListStates = new ArrayList<>();

    private String listName;

    public AgreementList(String stateListName, Agreement agreement) {
        this.listName = stateListName;
        this.agreement = agreement;
    }
}