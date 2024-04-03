package by.salary.authorizationserver.exception;

import org.apache.hc.client5.http.auth.AuthenticationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AbstractAuthenticationException {

    public UserNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getCode() {
        return HttpStatus.CONFLICT;
    }

}
