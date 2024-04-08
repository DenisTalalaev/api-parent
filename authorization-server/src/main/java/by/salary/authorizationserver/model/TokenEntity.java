package by.salary.authorizationserver.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false)
    private String username;

    private String verificationCode;

    @Column(nullable = false)
    private String authenticationToken;

    @LastModifiedDate
    private LocalDateTime modifiedOn;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    protected void prePersist() {
        if (this.createdOn == null) createdOn = LocalDateTime.now();
        if (this.modifiedOn == null) modifiedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.modifiedOn = LocalDateTime.now();
    }

}
