package by.salary.apigatewayv2.filter;

import by.salary.apigatewayv2.exception.AbstractStatusCodeAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.Map;

@Component
@Slf4j
@Order(-2)
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorHandler(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resourceProperties,
            ApplicationContext applicationContext,
            ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE);
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, options);
        Throwable throwable = getError(request);
        HttpStatusCode httpStatus = determineHttpStatus(throwable);

        errorPropertiesMap.put("status", httpStatus.value());
        errorPropertiesMap.remove("error");

        log.error("Error occurred: {}", errorPropertiesMap);
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }

    private HttpStatusCode determineHttpStatus(Throwable throwable) {
        if (throwable instanceof ResponseStatusException) {
            return ((ResponseStatusException) throwable).getStatusCode();
        } else if (throwable instanceof AbstractStatusCodeAuthenticationException) {
            return HttpStatus.resolve(((AbstractStatusCodeAuthenticationException) throwable).getStatusCode());
        } else if (throwable instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
