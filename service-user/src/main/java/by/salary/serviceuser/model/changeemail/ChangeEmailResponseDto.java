package by.salary.serviceuser.model.changeemail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailResponseDto {

    HttpStatus status;
    String message;
}
