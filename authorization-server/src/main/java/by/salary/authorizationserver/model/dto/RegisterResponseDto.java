package by.salary.authorizationserver.model.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterResponseDto {

    HttpStatus httpStatus;
    String message;

}