package by.salary.authorizationserver.exception;

import javax.naming.AuthenticationException;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
