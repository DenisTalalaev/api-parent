package by.salary.serviceuser.model.user;

import by.salary.serviceuser.entities.Authority;
import by.salary.serviceuser.entities.Organisation;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequestDTO {
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

    private String invitationCode;


    public Boolean isEnabled() {
        return isEnabled;
    }

    public Boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public Boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public Boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public String toString() {
        return "UserRequestDTO{" +
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
                ", invitationCode='" + invitationCode + '\'' +
                '}';
    }
}
