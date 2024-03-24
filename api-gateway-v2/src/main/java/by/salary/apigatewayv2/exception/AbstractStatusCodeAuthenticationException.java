package by.salary.apigatewayv2.exception;

import javax.naming.AuthenticationException;

public abstract class AbstractStatusCodeAuthenticationException extends AuthenticationException {


    public AbstractStatusCodeAuthenticationException(String message) {
        super(message);
    }
    public abstract int getStatusCode();
}
