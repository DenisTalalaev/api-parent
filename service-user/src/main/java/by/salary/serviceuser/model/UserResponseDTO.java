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
public class UserResponseDTO {

    public String userFirstName;
    public String userSurname;
    public String userSecondName;
    public String userEmail;
    public String userLogin;
    public String userPassword;
    public Boolean userIsVerified;
    public Boolean userIsSignedForNotifications;
    public BigInteger userOrganisationId;


}
