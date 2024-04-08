package by.salary.servicemail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailResponseDTO {
    HttpStatus status;
    String message;

    public MailResponseDTO(boolean b) {
        this.status = HttpStatus.OK;
        this.message = "Mail sent";
    }
}
