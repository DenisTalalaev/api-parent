package by.salary.authorizationserver.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Data
@Builder
@Getter
@Setter
public class AuthenticationChangePasswordResponseDto {

    HttpStatus status;
    String message;
}
