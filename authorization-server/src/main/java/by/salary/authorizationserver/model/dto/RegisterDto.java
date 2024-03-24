package by.salary.authorizationserver.model.dto;

import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RegisterDto {

    String email;
    String password;
    String pictureUri;
    List<String> authorities;
    AuthenticationRegistrationId authenticationRegistrationId;

}
