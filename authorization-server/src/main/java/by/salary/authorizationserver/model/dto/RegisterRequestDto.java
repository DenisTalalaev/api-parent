package by.salary.authorizationserver.model.dto;

import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RegisterRequestDto {

    AuthenticationRegistrationId authenticationRegistrationId;

    //local/oauth2
    String userEmail;

    //local
    String username;

    //local
    String userPassword;

    //always
    String invitationCode;

    //oatuth2
    String authenticationRegistrationKey;

    //oauth2
    String pictureUri;

}
