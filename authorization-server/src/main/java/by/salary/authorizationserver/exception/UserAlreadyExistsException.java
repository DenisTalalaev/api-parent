package by.salary.authorizationserver.exception;

import org.springframework.http.HttpStatus;

import javax.naming.AuthenticationException;

public class UserAlreadyExistsException extends AbstractAuthenticationException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getCode() {
        return HttpStatus.CONFLICT;
    }
}
