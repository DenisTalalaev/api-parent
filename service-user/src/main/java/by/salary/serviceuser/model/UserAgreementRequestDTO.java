package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.User;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserAgreementRequestDTO {
    private BigInteger id;
    private BigInteger userId;
    private BigInteger agreementId;
    private String moderatorName;
    private String moderatorComment;
    private BigDecimal count;
    private Time time;
    private BigDecimal currentBaseReward;

}
