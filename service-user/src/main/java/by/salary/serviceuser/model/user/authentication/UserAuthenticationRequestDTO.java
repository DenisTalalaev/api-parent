package by.salary.serviceuser.model.user.authentication;

import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationRequestDTO {

    private AuthenticationRegistrationId authenticationRegistrationId;
    private String authorizationRegistrationKey;
    private String username;
    private String userEmail;

}
