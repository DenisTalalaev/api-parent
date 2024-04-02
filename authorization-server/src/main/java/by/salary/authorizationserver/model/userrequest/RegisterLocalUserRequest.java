package by.salary.authorizationserver.model.userrequest;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RegisterLocalUserRequest {

    String username;
    String email;
    String password;
    String invitationCode;

}
