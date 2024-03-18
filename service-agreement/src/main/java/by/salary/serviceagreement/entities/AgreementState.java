package by.salary.serviceagreement.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementState {
    private Long id;
    private String StateName;
    private String StateInfo;

}
