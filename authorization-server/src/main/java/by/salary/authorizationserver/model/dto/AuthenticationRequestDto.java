package by.salary.authorizationserver.model.dto;

import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuthenticationRequestDto {

    AuthenticationRegistrationId authenticationRegistrationId;
    //github/google

    String authenticationRegistrationKey;

    //local
    String login;
    String email;
    String password;

}
