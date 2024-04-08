package by.salary.authorizationserver.model.userrequest;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VerificationCodeRequest {

    String code;
}
