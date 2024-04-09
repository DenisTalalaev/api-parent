package by.salary.apigatewayv2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AuthenticationNotVerifiedException extends AbstractStatusCodeAuthenticationException{
    public AuthenticationNotVerifiedException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
