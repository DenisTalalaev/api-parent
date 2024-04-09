package by.salary.authorizationserver.model;

import by.salary.authorizationserver.model.entity.Authority;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConnValidationResponse {
    private HttpStatus status;
    private boolean isAuthenticated;
    private String methodType;
    private String email;
    private Collection<String> authorities;
    private String token;

    private boolean is2FEnabled;
    private boolean is2FVerified;
}