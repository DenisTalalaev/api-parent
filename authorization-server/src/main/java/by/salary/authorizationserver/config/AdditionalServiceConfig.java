package by.salary.authorizationserver.config;


import by.salary.authorizationserver.model.dto.RegisterDto;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.service.InMemoryAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AllArgsConstructor
public class AdditionalServiceConfig {

    @Bean
    AuthorizationService userService() {
        AuthorizationService service = new InMemoryAuthorizationService();
        service.save(RegisterDto.builder()
                .email("hotspot.by@gmail.com")
                .password("123")
                .authorities(List.of("USER"))
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .build());

        return service;
    }



}
