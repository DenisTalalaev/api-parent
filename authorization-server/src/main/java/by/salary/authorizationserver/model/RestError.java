package by.salary.authorizationserver.model;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RestError {

    HttpStatus httpStatus;
    String message;
}
