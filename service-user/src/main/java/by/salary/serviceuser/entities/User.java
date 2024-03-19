package by.salary.serviceuser.entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;

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
    private String userLogin;

    private String userPassword;

    @Email
    private String userEmail;

    @Value("false")
    private Boolean userIsBlocked;

    @Value("false")
    private Boolean userIsVerified;

    @Value("false")
    private Boolean userIsSignedForNotifications;

    @NotNull
    private BigInteger userOrganisationId;

    public User(String userFirstName, String userSurname, String userSecondName, String userLogin, String userPassword, String userEmail, BigInteger userOrganisationId) {
        this.userFirstName = userFirstName;
        this.userSurname = userSurname;
        this.userSecondName = userSecondName;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userOrganisationId = userOrganisationId;
    }
}
