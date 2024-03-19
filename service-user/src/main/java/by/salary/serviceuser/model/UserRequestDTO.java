package by.salary.serviceuser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequestDTO {


    private BigInteger id;
    private String userFirstName;
    private String userSurname;
    private String userSecondName;
    private String userLogin;
    private String userPassword;
    private String userEmail;
    private Boolean userIsBlocked;
    private Boolean userIsVerified;
    private Boolean userIsSignedForNotifications;
    private BigInteger userOrganisationId;
}
