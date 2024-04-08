package by.salary.apigatewayv2.filter;

import by.salary.apigatewayv2.exception.AuthenticationNotVerifiedException;
import by.salary.apigatewayv2.service.AuthenticationService;
import by.salary.apigatewayv2.util.JwtService;
import jakarta.ws.rs.core.SecurityContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    RouteValidator validator;
    AuthenticationService authenticationService;
    JwtService jwtService;

    AuthenticationFilter(RouteValidator validator,
                         AuthenticationService authenticationService,
                         JwtService jwtService) {
        super(Config.class);
        this.validator = validator;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
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

                            String token = response.getToken();
                            if (jwtService.is2FEnabled(token)){

                                if (!jwtService.is2FVerified(token)) {
                                    return Mono.error(new AuthenticationNotVerifiedException("2FA is not verified"));
                                }
                            }

                            if (!jwtService.hasAuthority(token, config.getAuthority())) {
                                return Mono.error(new AuthenticationException("No authorities to preform this operation"));
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

    @Data
    public static class Config {
        String authority;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("authority");
    }
}
