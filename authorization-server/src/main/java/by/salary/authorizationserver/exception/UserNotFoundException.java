package by.salary.authorizationserver.exception;

import org.apache.hc.client5.http.auth.AuthenticationException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }


}
