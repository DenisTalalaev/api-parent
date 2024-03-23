package by.salary.apigateway.filter;

import by.salary.apigateway.model.Authorities;
import by.salary.apigateway.model.ConnValidationResponse;
import by.salary.apigateway.model.ExceptionResponseModel;
import by.salary.apigateway.util.SecurityConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationPrefilter extends AbstractGatewayFilterFactory<AuthenticationPrefilter.Config> {

    @Value("${excluded.urls}")
    List<String> excludedUrls;

    private final WebClient.Builder webClientBuilder;

    private ObjectMapper objectMapper;

    @Autowired
    public AuthenticationPrefilter(WebClient.Builder webClientBuilder,
                                   List<String> excludedUrls,
                                   ObjectMapper objectMapper) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.excludedUrls = excludedUrls;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            log.info("**************************************************************************");
            log.info("URL is - " + request.getURI().getPath());
            String bearerToken = request.getHeaders().getFirst(SecurityConstants.HEADER);
            log.info("Bearer Token: " + bearerToken);

            if (isSecured.test(request)) {
                return webClientBuilder.build().get()
                        .uri("lb://identity-server/auth/validate")
                        .header(SecurityConstants.HEADER, bearerToken)
                        .retrieve().bodyToMono(ConnValidationResponse.class)
                        .map(response -> {
                            exchange.getRequest().mutate().header("username", response.getUsername());
                            exchange.getRequest().mutate().header("authorities", response.getAuthorities()
                                    .stream().map(Authorities::getAuthority).reduce("", (a, b) -> a + "," + b));
                            exchange.getRequest().mutate().header("auth-token", response.getToken());

                            return exchange;
                        }).flatMap(chain::filter).onErrorResume(error -> {
                            log.info("Error Happened");
                            HttpStatus errorCode = null;
                            String errorMsg = "";
                            if (error instanceof WebClientResponseException webCLientException) {
                                errorCode = HttpStatus.valueOf(webCLientException.getStatusCode().value());
                                errorMsg = webCLientException.getStatusText();

                            } else {
                                errorCode = HttpStatus.BAD_GATEWAY;
                                errorMsg = HttpStatus.BAD_GATEWAY.getReasonPhrase();
                            }
//                            AuthorizationFilter.AUTH_FAILED_CODE
                            return onError(exchange, String.valueOf(errorCode.value()), errorMsg,
                                    "JWT Authentication Failed", errorCode);
                        });
            }

            return chain.filter(exchange);
        };
    }

    public Predicate<ServerHttpRequest> isSecured = request -> excludedUrls.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

    private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err, String errDetails, HttpStatus httpStatus) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
//        ObjectMapper objMapper = new ObjectMapper();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        try {
            response.getHeaders().add("Content-Type", "application/json");
            ExceptionResponseModel data = new ExceptionResponseModel(errCode, err, errDetails, null, new Date());
            byte[] byteData = objectMapper.writeValueAsBytes(data);
            return response.writeWith(Mono.just(byteData).map(t -> dataBufferFactory.wrap(t)));

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        return response.setComplete();
    }

    @NoArgsConstructor
    public static class Config {}
}
