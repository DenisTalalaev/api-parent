package by.salary.authorizationserver.model.dto;

import by.salary.authorizationserver.model.entity.MailType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailRequestDTO {
    String message;
    MailType mailType;
    String mailTo;
}