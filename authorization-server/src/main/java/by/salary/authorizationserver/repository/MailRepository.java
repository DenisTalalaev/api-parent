package by.salary.authorizationserver.repository;

import by.salary.authorizationserver.model.dto.MailRequestDTO;
import by.salary.authorizationserver.model.dto.MailResponseDTO;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatusCode;

import java.time.Duration;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MailRepository {
    WebClient.Builder webClientBuilder;

    public boolean checkEmail(MailRequestDTO mailRequestDTO){
        Optional<ResponseEntity<Void>> response = webClientBuilder.build()
                .post()
                .uri("lb://service-mail/mail/check")
                .body(Mono.just(mailRequestDTO), MailRequestDTO.class)
                .retrieve()
                .toBodilessEntity()
                .blockOptional(Duration.ofSeconds(10));

        if(response.isEmpty()){
            throw new InternalServerErrorException("Mail server error while checking email");
        }

        return response.get().getStatusCode().is2xxSuccessful();
    }

    public Optional<MailResponseDTO> sendMessage(MailRequestDTO mailRequestDTO, String jwt){
        Optional<MailResponseDTO> response = webClientBuilder.build()
                .post()
                .uri("lb://service-mail/mail/notification")
                .header("Authorization", "Bearer " + jwt)
                .body(Mono.just(mailRequestDTO), MailRequestDTO.class)
                .retrieve()
                .bodyToMono(MailResponseDTO.class)
                .blockOptional(Duration.ofSeconds(10));
        if (response.isEmpty()){
            throw new InternalServerErrorException("Mail server error while sending message");
        }
        return response;
    }
}
