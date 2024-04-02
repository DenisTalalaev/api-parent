package by.salary.serviceinvitation.exceptions;

import org.springframework.http.HttpStatus;

public class InvitationNotFoundException extends AbstractException {

    private final HttpStatus status;


    public InvitationNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
