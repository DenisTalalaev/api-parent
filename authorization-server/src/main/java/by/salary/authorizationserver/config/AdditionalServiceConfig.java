package by.salary.authorizationserver.config;


import by.salary.authorizationserver.exception.UserAlreadyExistsException;
import by.salary.authorizationserver.model.dto.RegisterDto;
import by.salary.authorizationserver.service.InMemoryAuthorizationService;
import by.salary.authorizationserver.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@AllArgsConstructor
public class AdditionalServiceConfig {

    @Bean
    AuthorizationService userService() throws UserAlreadyExistsException {
        AuthorizationService service = new InMemoryAuthorizationService();
        service.save(RegisterDto.builder()
                .email("hotspot.by@gmail.com")
                .password("123")
                .build());

        return service;
    }



}
