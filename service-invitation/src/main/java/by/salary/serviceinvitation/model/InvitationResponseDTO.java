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


    private String userFirstName;
    private String userSecondName;
    private String userSurname;

    private BigInteger organisationId;

    private String invitationCode;

    public InvitationResponseDTO(Invitation invitation) {
        this.id = invitation.getId();
        this.organisationId = invitation.getOrganisationId();
        this.invitationCode = invitation.getInvitationCode();

        this.userFirstName = invitation.getUserFirstName();
        this.userSecondName = invitation.getUserSecondName();
        this.userSurname = invitation.getUserSurname();
    }
}
