package by.salary.serviceuser.model.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailRequestDTO {
    String mailTo;
    String message;
    MailType mailType;
}
