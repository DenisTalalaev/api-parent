package by.salary.serviceuser.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
User
One table with all users
User=
    userFirstName
    userSurname
    userSecondName
    userLogin - unique
    userPassword - hash
    userEmail - unique
    userIsBlocked - status
    userIsVerified - status, if user is verified its email
    userOrganisationId - FOR WHICH organisation id

 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private BigInteger id;
    private String userFirstName;
    private String userSurname;
    private String userSecondName;
    private String userLogin;
    private String userPassword;
    private String userEmail;
    private Boolean userIsBlocked;
    private Boolean userIsVerified;
    private BigInteger userOrganisationId;
}
