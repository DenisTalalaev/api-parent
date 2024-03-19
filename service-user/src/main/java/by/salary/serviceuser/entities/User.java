package by.salary.serviceuser.entities;


import by.salary.serviceuser.model.UserRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
User
One table with all users
User=
    userFirstName
    userSurname
    userSecondName
    userLogin - unique
    userPassword - hash
    userEmail - unique
    userIsBlocked - status
    userIsVerified - status, if user is verified its email
    userIsSignedForNotifications - status if user signed for notifications
    userOrganisationId - FOR WHICH organisation id

 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @Size(min = 2, max = 50)
    private String userFirstName;

    @Size(min = 2, max = 50)
    private String userSurname;

    @Size(min = 2, max = 50)
    private String userSecondName;

    @Size(min = 8, max = 50)
    @Column(unique = true)
    private String username;

    private String userPassword;

    @Email
    private String userEmail;

    @Value("true")
    private Boolean isAccountNonExpired;

    @Value("true")
    private Boolean isAccountNonLocked;

    @Value("true")
    private Boolean isCredentialsNonExpired;

    @Value("true")
    private Boolean isEnabled;

    @NotNull
    private BigInteger userOrganisationId;

    private String authorities;

    public Boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public Boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public Boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public User(UserRequestDTO userRequestDTO) {

        this.userFirstName = userRequestDTO.getUserFirstName();
        this.userSurname = userRequestDTO.getUserSurname();
        this.userSecondName = userRequestDTO.getUserSecondName();
        this.username = userRequestDTO.getUsername();
        this.userPassword = userRequestDTO.getUserPassword();
        this.userEmail = userRequestDTO.getUserEmail();
        this.userOrganisationId = userRequestDTO.getUserOrganisationId();
        this.authorities = userRequestDTO.getAuthorities();
    }

    public void update(UserRequestDTO userRequestDTO) {
        this.userFirstName = userRequestDTO.getUserFirstName() == null? this.userFirstName : userRequestDTO.getUserFirstName();
        this.userSurname = userRequestDTO.getUserSurname() == null? this.userSurname : userRequestDTO.getUserSurname();
        this.userSecondName = userRequestDTO.getUserSecondName() == null? this.userSecondName : userRequestDTO.getUserSecondName();
        this.username = userRequestDTO.getUsername() == null? this.username : userRequestDTO.getUsername();
        this.userPassword = userRequestDTO.getUserPassword() == null? this.userPassword : userRequestDTO.getUserPassword();
        this.userEmail = userRequestDTO.getUserEmail() == null? this.userEmail : userRequestDTO.getUserEmail();
        this.userOrganisationId = userRequestDTO.getUserOrganisationId() == null? this.userOrganisationId : userRequestDTO.getUserOrganisationId();
        this.authorities = userRequestDTO.getAuthorities() == null? this.authorities : userRequestDTO.getAuthorities();
    }
}
