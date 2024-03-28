package by.salary.authorizationserver.model.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RegisterRequest {

    String username;
    String email;
    String password;

}
