package by.salary.authorizationserver.filter;

import by.salary.authorizationserver.model.RestError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import  org.springframework.security.core.AuthenticationException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "IllegalArgumentException";
        return handleExceptionInternal(
                ex,
                bodyOfResponse,
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                request
        );
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleAuthenticationException(Exception ex) {

        RestError re = new RestError(HttpStatus.UNAUTHORIZED,
                "Authentication failed");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }
}