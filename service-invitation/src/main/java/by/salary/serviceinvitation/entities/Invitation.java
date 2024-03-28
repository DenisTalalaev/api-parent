package by.salary.serviceinvitation.entities;


import by.salary.serviceinvitation.model.InvitationRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private BigInteger userId;

    @NotNull
    private BigInteger organisationId;

    @NotNull
    @Size(min = 9, max = 9)
    @Column(unique = true)
    private String invitationCode;

    public Invitation(InvitationRequestDTO invitationRequestDTO, BigInteger userId, String invitationCode) {
        this.userId = userId;
        this.organisationId = invitationRequestDTO.getOrganisationId();
        this.invitationCode = invitationCode;
    }
}
