package by.salary.authorizationserver.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthenticationException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
