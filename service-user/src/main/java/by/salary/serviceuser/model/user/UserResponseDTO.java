package by.salary.serviceuser.model.user;

import by.salary.serviceuser.entities.*;
import by.salary.serviceuser.model.premission.PermissionResponseDTO;
import by.salary.serviceuser.model.user.agreement.UserAgreementResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
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

    private List<PermissionResponseDTO> permissions;

    private List<UserAgreementResponseDTO> userAgreementList;

    public UserResponseDTO(User user) {

        Organisation organisation = user.getOrganisation();

        if (organisation != null) {
            this.organisationId = organisation.getId();
        }

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
        this.authorities = user.getAuthorities();
        this.permissions =  new ArrayList<>();
        user.getPermissions().forEach(permission -> this.permissions.add(new PermissionResponseDTO(permission)));
        this.userAgreementList = new ArrayList<>();
        user.getUserAgreementList().forEach(userAgreement -> this.userAgreementList.add(new UserAgreementResponseDTO(userAgreement)));
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
                ", authorities=" + authorities.toString() +
                ", permissions=" + permissions.toString() +
                ", userAgreementList=" + userAgreementList.toString() +
                '}';
    }
}
