package by.salary.serviceinvitation.exceptions;

import org.springframework.http.HttpStatus;

public class NotEnoughtPermissionsException extends AbstractException{
    private HttpStatus status;

    public NotEnoughtPermissionsException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

    @Override
    public HttpStatus getStatus() {

        return status;
    }
}
