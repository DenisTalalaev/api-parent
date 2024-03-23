package by.salary.authorizationserver.model;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenEntity {

    private String id;

    private String username;
    private String authenticationToken;
    private String modifiedBy;
    private LocalDateTime modifiedOn;
    private String createdBy;
    private LocalDateTime createdOn;
}
