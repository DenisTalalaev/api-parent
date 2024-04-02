package by.salary.serviceuser.exceptions;

import org.springframework.http.HttpStatus;

public class PermissionNotFoundException extends AbstractException{

    private final HttpStatus status;

    public PermissionNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }


    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
