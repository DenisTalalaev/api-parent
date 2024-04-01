package by.salary.authorizationserver.config;

import by.salary.authorizationserver.filter.*;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.CustomUserService;
import by.salary.authorizationserver.util.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    CustomUserService userDetailsService;
    ObjectMapper objectMapper;
    JwtService jwtService;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    AuthorizationRepository authorizationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(new UsernameEmailPasswordAuthenticationProvider(authorizationRepository, passwordEncoder()))
                .authenticationProvider(new JwtAuthenticationProvider(jwtService, authorizationRepository));
        return http
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
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new OAuth2AuthenticationSuccessHandler(authorizationRepository, jwtService))
                )
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
