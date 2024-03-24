package by.salary.apigatewayv2.filter;

import by.salary.apigatewayv2.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    RouteValidator validator;
    AuthenticationService authenticationService;

    AuthenticationFilter(RouteValidator validator, AuthenticationService authenticationService) {
        super(Config.class);
        this.validator = validator;
        this.authenticationService = authenticationService;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {

                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return Mono.error(new AuthenticationException("Missing authorization header"));
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return Mono.error(new AuthenticationException("Missing Bearer token"));
                }

                authHeader = authHeader.substring(7);

                //sending token to validation service
                return authenticationService.validate(authHeader)
                        .flatMap(response -> {
                            log.info("Authentication response: {}", response);
                            if (!response.isAuthenticated()) {
                                return Mono.error(new AuthenticationException("Unauthorized access to application"));
                            }
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> {
                            log.info("Error occurred during authentication: {}", e.getMessage());
                            return Mono.error(e);
                        });
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
    }


}
