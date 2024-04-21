package by.salary.serviceuser.exceptions;

import org.springframework.http.HttpStatus;

public class CustomValidationException extends AbstractException{
    public CustomValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
