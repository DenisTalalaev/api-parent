package by.salary.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConnValidationResponse {
    private String status;
    private boolean isAuthenticated;
    private String methodType;
    private String email;
    private List<String> authorities;
    private String token;
}
