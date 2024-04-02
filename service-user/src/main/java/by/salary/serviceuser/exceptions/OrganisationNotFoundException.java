package by.salary.serviceuser.exceptions;

import org.springframework.http.HttpStatus;

public class OrganisationNotFoundException extends AbstractException {

    HttpStatus status;

    public OrganisationNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
