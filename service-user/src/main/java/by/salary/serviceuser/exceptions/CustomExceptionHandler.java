package by.salary.serviceuser.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value =  AbstractException.class)
    public ResponseEntity<Object> handleException(AbstractException exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                exception.getMessage(),
                new HttpHeaders(),
                exception.getStatus(),
                request
        );
    }

    @ExceptionHandler(value =  SQLException.class)
    public ResponseEntity<Object> handleException(SQLException exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                exception.getMessage(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }
}
