package by.salary.serviceuser.model.changepassword;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationChangePasswordRequestDto {
    private String password;
    private String email;
}
