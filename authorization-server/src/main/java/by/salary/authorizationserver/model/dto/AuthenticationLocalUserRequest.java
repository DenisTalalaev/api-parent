package by.salary.authorizationserver.model.dto;

import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class AuthenticationLocalUserRequest {

    private AuthenticationRegistrationId authenticationRegistrationId;

    private String authorizationRegistrationKey;

    private String username;

    private String userEmail;

    private String password;
}
