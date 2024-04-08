package by.salary.servicemail.exceptions;

import org.springframework.http.HttpStatus;

public class MailSendingException extends AbstractException {

    HttpStatus status;
    public MailSendingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
