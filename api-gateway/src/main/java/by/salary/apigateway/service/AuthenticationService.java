package by.salary.apigateway.service;

import by.salary.apigateway.model.AuthenticationDto;
import by.salary.apigateway.model.ConnValidationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Service
@AllArgsConstructor
public class AuthenticationService {

    WebClient.Builder webClientBuilder;

    public ConnValidationResponse authenticate(AuthenticationDto authenticationDto) {
        return webClientBuilder.build()
                .post()
                .uri("lb://identity-server/auth/token")
                .body(authenticationDto, AuthenticationDto.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> switch (response.statusCode().value()) {
                            case 400 ->
                                    Mono.error(new AuthenticationException("The request was invalid."));
                            case 401, 403 ->
                                    Mono.error(new AuthenticationException("The request did not include an authentication token or the authentication token was expired."));
                            case 404 ->
                                    Mono.error(new AuthenticationException("The requested resource was not found."));
                            case 500 ->
                                    Mono.error(new AuthenticationException("The request was not completed due to an internal error on the server side."));
                            default ->
                                    Mono.error(new AuthenticationException("Authentication failed"));
                        })
                .bodyToMono(ConnValidationResponse.class)
                .block();
    }
}
