package by.salary.authorizationserver.model;

import lombok.*;

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
    private List<String> authorities;
    private String token;
}