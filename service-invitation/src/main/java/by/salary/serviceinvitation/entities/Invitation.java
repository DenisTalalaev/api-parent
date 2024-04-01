package by.salary.serviceinvitation.entities;


import by.salary.serviceinvitation.model.InvitationRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
Invitation
One table with all invitations.
Invitation=
    userId - WHO id recipient
    organisationId - FOR WHICH organisation id recipient
    invitationCode - CODE of 9 symbols (numbers)
UserId = User created in the time of creation of the invitation
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @NotNull
    private BigInteger organisationId;

    private String userFirstName;
    private String userSecondName;
    private String userSurname;

    @NotNull
    @Column(unique = true)
    private String invitationCode;


    public Invitation(InvitationRequestDTO invitationRequestDTO, String s) {
        this.organisationId = invitationRequestDTO.getOrganisationId();
        this.userFirstName = invitationRequestDTO.getUserFirstName();
        this.userSecondName = invitationRequestDTO.getUserSecondName();
        this.userSurname = invitationRequestDTO.getUserSurname();
        this.invitationCode = s;
    }

    @Override
    public String toString() {
        return id + ";" + organisationId + ";" + userFirstName + ";" + userSecondName + ";" + userSurname + ";" + invitationCode;
    }
}
