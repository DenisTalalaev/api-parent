package by.salary.authorizationserver.repository;

import by.salary.authorizationserver.exception.UserAlreadyExistsException;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HttpAuthorizationRepository implements AuthorizationRepository {
    WebClient.Builder webClientBuilder;

    @Override
    public Optional<AuthenticationResponseDto> find(AuthenticationRequestDto authenticationRequestDto) {
        Optional<AuthenticationResponseDto> response = webClientBuilder.build()
                .post()
                .uri("lb://service-user/account/auth")
                .body(Mono.just(authenticationRequestDto), AuthenticationRequestDto.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        resp -> switch (resp.statusCode().value()) {
                            case 400 ->
                                    Mono.error(new AuthenticationException("The request was invalid."));
                            case 404 ->
                                    Mono.error(new AuthenticationException("The requested resource was not found."));
                            case 500 ->
                                    Mono.error(new AuthenticationException("The request was not completed due to an internal error on the server side."));
                            default ->
                                    Mono.error(new AuthenticationException("Authentication failed"));
                        })
                .bodyToMono(AuthenticationResponseDto.class)
                .blockOptional();
        return response;
    }

    @Override
    public RegisterResponseDto save(RegisterRequestDto newUser) {
        Optional<RegisterResponseDto> response = webClientBuilder.build()
                .post()
                .uri("lb://service-user/account/reg")
                .body(Mono.just(newUser), RegisterRequestDto.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        resp -> switch (resp.statusCode().value()) {
                    case 400 -> Mono.error(new AuthenticationException("The request was invalid."));
                    case 404 -> Mono.error(new AuthenticationException("The requested resource was not found."));
                    case 409 -> Mono.error(new UserAlreadyExistsException("User already exists"));
                    default -> Mono.error(new InternalServerErrorException("Internal server error"));}
                )
                .bodyToMono(RegisterResponseDto.class)
                .blockOptional();
        if (response.isEmpty()) {
            throw new UserAlreadyExistsException("User not found");
        }
        return response.get();
    }
}
