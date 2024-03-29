package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.Authority;
import by.salary.serviceuser.entities.Organisation;
import by.salary.serviceuser.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

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

    private BigInteger organisationId;

    private List<Authority> authorities;

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
        this.organisationId = user.getOrganisation().getId();
        this.authorities = user.getAuthorities();
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", userFirstName='" + userFirstName + '\'' +
                ", userSurname='" + userSurname + '\'' +
                ", userSecondName='" + userSecondName + '\'' +
                ", username='" + username + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                ", organisationId=" + organisationId +
                ", authorities=" + authorities +
                '}';
    }
}
