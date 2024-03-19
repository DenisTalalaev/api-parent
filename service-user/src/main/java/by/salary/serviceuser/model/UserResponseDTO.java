package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private BigInteger id;
    private String userFirstName;

    private String userSurname;
    private String userSecondName;
    private String username;

    private String userPassword;

    private String userEmail;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Boolean isEnabled;

    private BigInteger userOrganisationId;

    private String authorities;

    public UserResponseDTO(User user) {

        this.id = user.getId();
        this.userFirstName = user.getUserFirstName();
        this.userSurname = user.getUserSurname();
        this.userSecondName = user.getUserSecondName();
        this.username = user.getUsername();
        this.userPassword = user.getUserPassword();
        this.userEmail = user.getUserEmail();
        this.isAccountNonExpired = user.isAccountNonExpired();
        this.isAccountNonLocked = user.isAccountNonLocked();
        this.isCredentialsNonExpired = user.isCredentialsNonExpired();
        this.isEnabled = user.isEnabled();
        this.userOrganisationId = user.getUserOrganisationId();
        this.authorities = user.getAuthorities();
    }
}
