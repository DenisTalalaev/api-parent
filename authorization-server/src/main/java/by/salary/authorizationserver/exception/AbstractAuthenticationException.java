package by.salary.authorizationserver.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractAuthenticationException extends RuntimeException {

    public AbstractAuthenticationException(String message) {
        super(message);
    }

    public abstract HttpStatus getCode();
}
