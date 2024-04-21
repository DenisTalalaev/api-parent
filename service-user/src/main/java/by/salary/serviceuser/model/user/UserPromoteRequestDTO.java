package by.salary.serviceuser.model.user;

import by.salary.serviceuser.entities.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPromoteRequestDTO {

    private Authority authority;

    private String username;

}
