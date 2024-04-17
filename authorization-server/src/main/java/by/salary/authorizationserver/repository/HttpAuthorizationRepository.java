package by.salary.authorizationserver.repository;

import by.salary.authorizationserver.exception.UserAlreadyExistsException;
import by.salary.authorizationserver.model.dto.*;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
                .onStatus(HttpStatusCode::isError, resp -> Mono.empty())
                .bodyToMono(AuthenticationResponseDto.class)
                .blockOptional();

        if (response.isPresent()){
            if (response.get().getUserEmail() == null) {
                return Optional.empty();
            }
        }

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
                    case 400 -> Mono.error(new BadRequestException("The request was invalid."));
                    case 404 -> Mono.error(new NotFoundException("The requested resource was not found."));
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

    public AuthenticationChangePasswordResponseDto changePassword(AuthenticationChangePasswordRequestDto changePasswordRequestDto) {
        Optional<AuthenticationChangePasswordResponseDto> response = webClientBuilder.build()
                .put()
                .uri("lb://service-user/users/changepassword")
                .body(Mono.just(changePasswordRequestDto), AuthenticationChangePasswordRequestDto.class)
                .retrieve()
                .bodyToMono(AuthenticationChangePasswordResponseDto.class)
                .blockOptional(Duration.ofSeconds(10));
        if (response.isEmpty()) {
            throw new UserAlreadyExistsException("User not found");
        }
        return response.get();
    }
}
