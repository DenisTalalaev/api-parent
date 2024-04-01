package by.salary.serviceuser.entities;

import lombok.*;
import java.math.BigInteger;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    private BigInteger id;

    private BigInteger organisationId;

    private String userFirstName;
    private String userSecondName;
    private String userSurname;

    private String invitationCode;

    public Invitation(String block) {
        String[] strings = block.split(";");
        this.id = new BigInteger(strings[0]);
        this.organisationId = new BigInteger(strings[1]);
        this.userFirstName = strings[2];
        this.userSecondName = strings[3];
        this.userSurname = strings[4];
        this.invitationCode = strings[5];
    }
}
