package by.salary.serviceuser.model;

import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestDTO {

    AuthenticationRegistrationId authenticationRegistrationId;

    //local/oauth2
    String userEmail;

    //local
    String username;

    //local
    String userPassword;


    //oatuth2
    String authenticationRegistrationKey;

    //oauth2
    String pictureUri;

    boolean is2FEnabled;
    

}
