package by.salary.serviceuseragreement.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;

/**
User agreement
One table with all user agreements
UserAgreement=
    userId - WHO take the add
    agreementId - WHAT agreement (class AgreementState - service-agreement module)
    moderatorName (just Name of user, who assigned the add)
    moderatorComment (moderator comment, text )
    count (the count of %)
    time (when the add was assigned -set in create time)
    currentBaseReward (current base reward from ORGANISATION property -set in create time)
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAgreement {
    private BigInteger id;
    private BigInteger userId;
    private BigInteger agreementId;
    private String moderatorName;
    private String moderatorComment;
    private BigDecimal count;
    private Time time;
    private BigDecimal currentBaseReward;
}
