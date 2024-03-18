package by.salary.serviceinvitation.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

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
