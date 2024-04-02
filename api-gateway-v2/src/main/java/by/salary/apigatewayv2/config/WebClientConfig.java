package by.salary.apigatewayv2.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}