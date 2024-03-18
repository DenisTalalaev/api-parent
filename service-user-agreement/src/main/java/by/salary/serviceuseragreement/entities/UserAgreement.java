package by.salary.serviceuseragreement.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;

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
}
