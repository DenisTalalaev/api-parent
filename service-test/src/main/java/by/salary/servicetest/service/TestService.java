package by.salary.servicetest.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class TestService {

    WebClient.Builder webClientBuilder;

    public String test() {
        Integer id = webClientBuilder.build()
                .get()
                .uri("http://service-test/test/{id}", 1)
                .retrieve()
                .bodyToMono(Integer.class)
                .block();

        return "Test service " + id;
    }
}
