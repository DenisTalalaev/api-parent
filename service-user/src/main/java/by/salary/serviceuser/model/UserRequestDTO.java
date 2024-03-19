package by.salary.serviceuser.model;

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

    private BigInteger userOrganisationId;

    private String authorities;
}
