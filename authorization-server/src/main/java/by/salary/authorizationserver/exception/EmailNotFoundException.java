package by.salary.authorizationserver.exception;

import org.springframework.http.HttpStatus;

import javax.naming.AuthenticationException;

public class EmailNotFoundException extends AbstractAuthenticationException {


    public EmailNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getCode() {
        return HttpStatus.CONFLICT;
    }
}
