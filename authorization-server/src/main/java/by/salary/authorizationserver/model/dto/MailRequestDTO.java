package by.salary.authorizationserver.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailRequestDTO {
    String message;

    String mailTo;
}