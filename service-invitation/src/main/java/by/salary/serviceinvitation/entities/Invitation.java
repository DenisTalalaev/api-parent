package by.salary.serviceinvitation.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
Invitation
One table with all invitations.
Invitation=
    userId - WHO id recipient
    organisationId - FOR WHICH organisation id recipient
    invitationCode - CODE
UserId = User created in the time of creation of the invitation
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    private BigInteger id;
    private BigInteger userId;
    private BigInteger organisationId;
    private String invitationCode;
}
