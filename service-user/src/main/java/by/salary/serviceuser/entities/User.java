package by.salary.serviceuser.entities;


import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import by.salary.serviceuser.model.user.UserRequestDTO;
import by.salary.serviceuser.util.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * User
 * One table with all users
 * User=
 * userFirstName
 * userSurname
 * userSecondName
 * userLogin - unique
 * userPassword - hash
 * userEmail - unique
 * userIsBlocked - status
 * userIsVerified - status, if user is verified its email
 * userIsSignedForNotifications - status if user signed for notifications
 * userOrganisationId - FOR WHICH organisation id
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
    @Convert(converter = AttributeEncryptor.class)
    private String userFirstName;

    @Size(min = 2, max = 50)
    @Convert(converter = AttributeEncryptor.class)
    private String userSurname;

    @Size(min = 2, max = 50)
    @Convert(converter = AttributeEncryptor.class)
    private String userSecondName;



    @Size(min = 8, max = 50)
    @Column(unique = true)
    @Convert(converter = AttributeEncryptor.class)
    private String username;

    private String userPassword;




    @Email
    @Column(unique = true)
    @Convert(converter = AttributeEncryptor.class)
    private String userEmail;




    @Value("true")
    private Boolean isAccountNonExpired;

    @Value("true")
    private Boolean isAccountNonLocked;

    @Value("true")
    private Boolean isCredentialsNonExpired;

    @Value("true")
    private Boolean isEnabled;

    private Boolean is2FVerified;

    private Boolean is2FEnabled;




    private String imageURI;

    @Column(unique = true)
    private String userAuthorisationAttributeKey;

    @Enumerated(EnumType.STRING)
    private AuthenticationRegistrationId authenticationRegistrationId;



    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_permission",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "useragreement_id")
    private List<UserAgreement> userAgreementList;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Authority> authorities;



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

    public User(UserRequestDTO userRequestDTO, Organisation organisation) {

        this.userFirstName = userRequestDTO.getUserFirstName();
        this.userSurname = userRequestDTO.getUserSurname();
        this.userSecondName = userRequestDTO.getUserSecondName();
        this.username = userRequestDTO.getUsername();
        this.userPassword = userRequestDTO.getUserPassword();
        this.userEmail = userRequestDTO.getUserEmail();
        this.organisation = organisation;
        this.authorities = userRequestDTO.getAuthorities() == null ? new ArrayList<>(): userRequestDTO.getAuthorities();
        this.isEnabled = userRequestDTO.isEnabled() != null && userRequestDTO.isEnabled();
        this.isAccountNonExpired = userRequestDTO.isAccountNonExpired() == null || userRequestDTO.isAccountNonExpired();
        this.isAccountNonLocked = userRequestDTO.isAccountNonLocked() == null || userRequestDTO.isAccountNonLocked();
        this.isCredentialsNonExpired = userRequestDTO.isCredentialsNonExpired() == null || userRequestDTO.isCredentialsNonExpired();

    }

    public void update(UserRequestDTO userRequestDTO, Organisation organisation) {
        this.userFirstName = userRequestDTO.getUserFirstName() == null ? this.userFirstName : userRequestDTO.getUserFirstName();
        this.userSurname = userRequestDTO.getUserSurname() == null ? this.userSurname : userRequestDTO.getUserSurname();
        this.userSecondName = userRequestDTO.getUserSecondName() == null ? this.userSecondName : userRequestDTO.getUserSecondName();
        this.username = userRequestDTO.getUsername() == null ? this.username : userRequestDTO.getUsername();
        this.userPassword = userRequestDTO.getUserPassword() == null ? this.userPassword : userRequestDTO.getUserPassword();
        this.userEmail = userRequestDTO.getUserEmail() == null ? this.userEmail : userRequestDTO.getUserEmail();
        this.organisation = organisation == null ? this.organisation : organisation;
        this.authorities = userRequestDTO.getAuthorities() == null ? this.authorities : userRequestDTO.getAuthorities();
        this.isEnabled = userRequestDTO.isEnabled() == null ? this.isEnabled : userRequestDTO.isEnabled();
        this.isAccountNonExpired = userRequestDTO.isAccountNonExpired() == null ? this.isAccountNonExpired : userRequestDTO.isAccountNonExpired();
        this.isAccountNonLocked = userRequestDTO.isAccountNonLocked() == null ? this.isAccountNonLocked : userRequestDTO.isAccountNonLocked();
        this.isCredentialsNonExpired = userRequestDTO.isCredentialsNonExpired() == null ? this.isCredentialsNonExpired : userRequestDTO.isCredentialsNonExpired();
    }

    public void update(UserRequestDTO userRequestDTO) {
        this.userFirstName = userRequestDTO.getUserFirstName() == null? this.userFirstName : userRequestDTO.getUserFirstName();
        this.userSurname = userRequestDTO.getUserSurname() == null? this.userSurname : userRequestDTO.getUserSurname();
        this.userSecondName = userRequestDTO.getUserSecondName() == null? this.userSecondName : userRequestDTO.getUserSecondName();
        this.is2FEnabled = userRequestDTO.getIs2FAEnabled() == null ? this.is2FEnabled : userRequestDTO.getIs2FAEnabled();
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void newUser(User user2) {
        this.userPassword = user2.getUserPassword();
        this.userEmail = user2.getUserEmail();
        this.username = user2.getUsername();

        this.userAuthorisationAttributeKey = user2.getUserAuthorisationAttributeKey();
        this.authenticationRegistrationId = user2.getAuthenticationRegistrationId();

        this.isEnabled = user2.isEnabled();
        this.isAccountNonExpired = user2.isAccountNonExpired();
        this.isAccountNonLocked = user2.isAccountNonLocked();
        this.isCredentialsNonExpired = user2.isCredentialsNonExpired();

    }

    public void clear() {
        this.userPassword = null;
        this.userEmail = null;
        this.username = null;

        this.userAuthorisationAttributeKey = null;
        this.authenticationRegistrationId = null;

        this.isEnabled = false;
        this.isAccountNonExpired = false;
        this.isAccountNonLocked = false;
    }
}
