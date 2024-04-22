package by.salary.serviceagreement.config;

import by.salary.serviceagreement.service.DES;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManualBeanConfig {

    @Value("${database.secret}")
    String secret;
    @Bean
    public DES des() {
        return new DES(secret);
    }
}
