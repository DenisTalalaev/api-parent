package by.salary.serviceuser.model.user.agreement;

import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.entities.UserAgreement;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAgreementResponseDTO {

    private BigInteger id;
    private BigInteger userId;

    private BigInteger agreementListId;
    private String agreementListName;

    private BigInteger agreementStateId;
    private String agreementStateName;
    private String agreementStateInfo;


    private String agreementId;
    private String moderatorName;
    private String moderatorComment;
    private BigDecimal count;

    @JsonFormat(pattern = "E MMM dd yyyy HH:mm:ss 'GMT'Z", timezone = "GMT+3", locale = "US")
    private Date time;
    private BigDecimal currentBaseReward;

    public UserAgreementResponseDTO(UserAgreement userAgreement,

                                    BigInteger agreementListId,
                                    String agreementListName,

                                    BigInteger agreementStateId,
                                    String agreementStateName,
                                    String agreementStateInfo
    ) {

        this.agreementListId = agreementListId;
        this.agreementListName = agreementListName;

        this.agreementStateId = agreementStateId;
        this.agreementStateName = agreementStateName;
        this.agreementStateInfo = agreementStateInfo;

        this.id = userAgreement.getId();
        this.userId = userAgreement.getUser().getId();
        this.agreementId = agreementStateName + ": " + agreementStateInfo;
        this.moderatorName = userAgreement.getModeratorName();
        this.moderatorComment = userAgreement.getModeratorComment();
        this.count = userAgreement.getCount();
        this.time = userAgreement.getTime();
        this.currentBaseReward = userAgreement.getCurrentBaseReward();
    }
}
