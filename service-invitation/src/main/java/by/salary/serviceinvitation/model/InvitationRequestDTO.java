package by.salary.serviceinvitation.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvitationRequestDTO {

    private String userFirstName;
    private String userSecondName;
    private String userSurnameName;
    private BigInteger organisationId;
}
