package by.salary.authorizationserver.model.dto;

import lombok.*;
import org.springframework.stereotype.Service;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterResponseDto {

    boolean isRegistrationCompleted;
    String message;

}
