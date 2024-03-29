package by.salary.serviceinvitation.interfaces;

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
public class UserRequestDTO {
    private String userFirstName;
    private String userSurname;
    private String userSecondName;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private Boolean isEnabled;
    private BigInteger organisationId;

    public UserRequestDTO(String userFirstName, String userSecondName, String userSurname, BigInteger organisationId) {

        this.userFirstName = userFirstName;
        this.userSecondName = userSecondName;
        this.userSurname = userSurname;
        this.organisationId = organisationId;

        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = false;
    }

    @Override
    public String toString() {
        return "UserRequestDTO{" +
                "userFirstName='" + userFirstName + '\'' +
                ", userSecondName='" + userSecondName + '\'' +
                ", userSurname='" + userSurname + '\'' +
                ", organisationId=" + organisationId +
                '}';
    }
}
