package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.Authority;
import by.salary.serviceuser.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationResponseDTO {

    private String userName;

    private String userEmail;

    private String password;

    Collection<Authority> authorities;

    public UserAuthenticationResponseDTO(Optional<User> byAuthorisationAttributeKey) {
        this.userName = byAuthorisationAttributeKey.get().getUsername();
        this.userEmail = byAuthorisationAttributeKey.get().getUserEmail();
        this.password = byAuthorisationAttributeKey.get().getUserPassword();
        this.authorities = byAuthorisationAttributeKey.get().getAuthorities();
    }
}
