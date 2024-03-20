package by.salary.serviceinvitation.entities;


import by.salary.serviceinvitation.model.InvitationRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String invitationCode;

    public Invitation(InvitationRequestDTO invitationRequestDTO) {
        this.userId = invitationRequestDTO.getUserId();
        this.organisationId = invitationRequestDTO.getOrganisationId();
        this.invitationCode = invitationRequestDTO.getInvitationCode();
    }

    public void update(InvitationRequestDTO invitationRequestDTO) {
        this.userId = invitationRequestDTO.getUserId() == null? this.userId : invitationRequestDTO.getUserId();
        this.organisationId = invitationRequestDTO.getOrganisationId() == null? this.organisationId : invitationRequestDTO.getOrganisationId();
        this.invitationCode = invitationRequestDTO.getInvitationCode() == null? this.invitationCode : invitationRequestDTO.getInvitationCode();
    }
}
