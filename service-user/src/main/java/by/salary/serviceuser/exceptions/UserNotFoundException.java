package by.salary.serviceuser.exceptions;

import org.springframework.http.HttpStatus;
import reactor.netty.http.server.HttpServerState;

public class UserNotFoundException extends AbstractException {
    private final HttpStatus status;

    public UserNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }
    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
