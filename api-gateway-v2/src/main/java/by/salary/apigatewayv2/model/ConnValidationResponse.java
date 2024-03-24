package by.salary.apigatewayv2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class ConnValidationResponse {
    private String status;
    private boolean authenticated;
    private String methodType;
    private String email;
    private List<String> authorities;
    private String token;
}
