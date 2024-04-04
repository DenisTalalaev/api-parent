package by.salary.authorizationserver.config;

import by.salary.authorizationserver.authentication.OAuth2AuthenticationSuccessHandler;
import by.salary.authorizationserver.authentication.filter.JWTAuthenticationFilter;
import by.salary.authorizationserver.authentication.filter.JWTVerifierFilter;
import by.salary.authorizationserver.authentication.provider.JwtAuthenticationProvider;
import by.salary.authorizationserver.authentication.provider.UsernameEmailPasswordAuthenticationProvider;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.CustomUserService;
import by.salary.authorizationserver.util.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    ObjectMapper objectMapper;
    JwtService jwtService;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    AuthorizationRepository authorizationRepository;

    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(new UsernameEmailPasswordAuthenticationProvider(authorizationRepository, passwordEncoder()))
                .authenticationProvider(new JwtAuthenticationProvider(jwtService, authorizationRepository));
        return http
                .exceptionHandling(exceptionHandler -> exceptionHandler
                        .authenticationEntryPoint(authEntryPoint)
                )
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers("/auth/register", "/auth/token").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilter(new JWTAuthenticationFilter(
                        objectMapper, authenticationManagerBuilder.getOrBuild(), jwtService))
                .addFilterAfter(
                        new JWTVerifierFilter(jwtService, authorizationRepository, authenticationManagerBuilder.getOrBuild()),
                        JWTAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new OAuth2AuthenticationSuccessHandler(authorizationRepository, jwtService))
                        .failureHandler((request, response, exception) -> { throw exception; })
                )
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
