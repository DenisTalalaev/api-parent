package by.salary.authorizationserver.exception;

import javax.naming.AuthenticationException;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
