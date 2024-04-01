package by.salary.authorizationserver.model;

import by.salary.authorizationserver.model.entity.Authority;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConnValidationResponse {
    private String status;
    private boolean isAuthenticated;
    private String methodType;
    private String email;
    private Collection<Authority> authorities;
    private String token;
}