package by.salary.serviceuser.model.changepassword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class AuthenticationChangePasswordResponseDto {
    HttpStatus status;
    String message;

}
