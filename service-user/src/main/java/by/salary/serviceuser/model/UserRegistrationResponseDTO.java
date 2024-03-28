package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponseDTO {
    HttpStatus httpStatus;
    String message;

    public UserRegistrationResponseDTO(User user) {
        this.httpStatus = HttpStatus.OK;
        this.message = "User " + user.getUsername() + " was registered";
    }
}
