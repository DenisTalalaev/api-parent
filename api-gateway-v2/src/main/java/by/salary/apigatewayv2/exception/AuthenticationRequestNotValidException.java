package by.salary.apigatewayv2.exception;


public class AuthenticationRequestNotValidException extends AbstractStatusCodeAuthenticationException {

    public AuthenticationRequestNotValidException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return 400;
    }

}
