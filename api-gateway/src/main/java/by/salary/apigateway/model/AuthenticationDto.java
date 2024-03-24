package by.salary.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationDto {

    private String email;
    private String password;

}
