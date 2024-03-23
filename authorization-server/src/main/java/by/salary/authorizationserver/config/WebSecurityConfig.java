package by.salary.authorizationserver.config;

import by.salary.authorizationserver.filter.JWTAuthenticationFilter;
import by.salary.authorizationserver.filter.JWTVerifierFilter;
import by.salary.authorizationserver.filter.JwtAuthenticationProvider;
import by.salary.authorizationserver.model.dto.AuthDto;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.service.CustomUserService;
import by.salary.authorizationserver.util.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    CustomUserService userDetailsService;
    ObjectMapper objectMapper;
    JwtService jwtService;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    AuthorizationService authorizationService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(new JwtAuthenticationProvider(jwtService, authorizationService))
                .authenticationProvider(authenticationProvider());
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers("/auth/register", "/auth/token").permitAll()
                                .anyRequest().hasAuthority("USER")
                )
                .addFilter(new JWTAuthenticationFilter(
                        objectMapper, authenticationManagerBuilder.getOrBuild(), jwtService))
                .addFilterAfter(
                        new JWTVerifierFilter(jwtService, authorizationService, authenticationManagerBuilder.getOrBuild()),
                        JWTAuthenticationFilter.class)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
