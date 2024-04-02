package by.salary.serviceagreement.exceptions;

import org.springframework.http.HttpStatus;

public class AgreementNotFoundException extends AbstractException {
    private final HttpStatus status;

    public AgreementNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
