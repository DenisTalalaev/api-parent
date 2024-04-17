package by.salary.authorizationserver.model.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationChangePasswordRequestDto {

            String email;
    String password;
}
