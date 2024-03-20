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

    private BigInteger id;

    private BigInteger userId;

    private BigInteger organisationId;

    private String invitationCode;
}
