package by.salary.apigatewayv2.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(-10)
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("**************************************************************************");
            log.info("Path of the Request Received -----> " + exchange.getRequest().getPath());
            log.info("Request Method -----> " + exchange.getRequest().getMethod());
            log.info("Ip Address Of Requestor -----> " + exchange.getRequest().getRemoteAddress());
            log.info("Headers of the Request Received -----> " + exchange.getRequest().getHeaders());
            log.info("Authorization header -----> " + exchange.getRequest().getHeaders().get("Authorization"));
            log.info("**************************************************************************");

            return chain.filter(exchange);

            }
        );
    }

    public static class Config {


    }


}