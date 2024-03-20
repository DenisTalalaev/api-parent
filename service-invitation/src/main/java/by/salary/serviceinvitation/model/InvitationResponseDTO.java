package by.salary.serviceinvitation.model;

import by.salary.serviceinvitation.entities.Invitation;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvitationResponseDTO {

    private BigInteger id;

    private BigInteger userId;

    private BigInteger organisationId;

    private String invitationCode;

    public InvitationResponseDTO(Invitation invitation) {
        this.id = invitation.getId();
        this.userId = invitation.getUserId();
        this.organisationId = invitation.getOrganisationId();
        this.invitationCode = invitation.getInvitationCode();
    }
}
