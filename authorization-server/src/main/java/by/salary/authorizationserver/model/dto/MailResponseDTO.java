package by.salary.authorizationserver.model.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailResponseDTO {
    HttpStatus status;
    String message;
}
