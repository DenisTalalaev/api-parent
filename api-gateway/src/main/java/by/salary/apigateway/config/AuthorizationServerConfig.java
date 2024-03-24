package by.salary.apigateway.config;

import by.salary.apigateway.filter.BasicAuthenticationSuccessHandler;
import by.salary.apigateway.filter.CustomUsernamePasswordAuthenticationFilter;
import by.salary.apigateway.filter.OAuth2AuthenticationSuccessHandler;
import by.salary.apigateway.service.AuthenticationService;
import by.salary.apigateway.service.security.CustomUsernamePasswordAuthenticationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class AuthorizationServerConfig {

    BasicAuthenticationSuccessHandler basicSuccessHandler;

    OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    AuthenticationService authenticationService;
    ObjectMapper objectMapper;

    AuthenticationManagerBuilder authenticationManagerBuilder;

    /*@Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }*/

    @Bean
    public SecurityFilterChain configureSecurityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = this.authenticationManagerBuilder
                .authenticationProvider(new CustomUsernamePasswordAuthenticationProvider(authenticationService));
        return http
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/auth/register", "/auth/token").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .successHandler(basicSuccessHandler))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2SuccessHandler))
                .addFilter(new CustomUsernamePasswordAuthenticationFilter(objectMapper,
                        authenticationManagerBuilder.getOrBuild()))
                .build();
    }

}
