package by.salary.apigatewayv2.exception;

public class AuthenticationInvalidCredentialsException extends AbstractStatusCodeAuthenticationException {

    public AuthenticationInvalidCredentialsException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }

}
