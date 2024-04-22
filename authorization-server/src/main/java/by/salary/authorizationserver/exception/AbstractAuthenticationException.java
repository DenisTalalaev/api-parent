package by.salary.authorizationserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;


public abstract class AbstractAuthenticationException extends AuthenticationException {

    public AbstractAuthenticationException(String message) {
        super(message);
    }

    public abstract HttpStatus getCode();
}
