package by.salary.authorizationserver.filter;

import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.util.JwtService;
import by.salary.authorizationserver.util.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {



    private ObjectMapper objectMapper;

    private AuthenticationManager authenticationManager;
    JwtService jwtService;

    public JWTAuthenticationFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager,
                                   JwtService jwtService) {
        setFilterProcessesUrl("/auth/token");
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            AuthenticationRequestDto authDto = objectMapper.readValue(request.getInputStream(), AuthenticationRequestDto.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(),
                    authDto.getPassword()));

        } catch (IOException e) {
            throw new AuthenticationCredentialsNotFoundException("Could not found user", e);
        }
    }


    // this is called when user is successfully authenticated in auth/token
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String email = authResult.getName();
        List<String> authorities = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(toList());

        String token = jwtService.generateToken(email, authorities);

        //TODO: save token in database

        response.addHeader(SecurityConstants.HEADER, String.format("Bearer %s", token));
        response.addHeader("Expiration", String.valueOf(30*60));

        ConnValidationResponse respModel = ConnValidationResponse.builder()
                .status(HttpStatus.OK.name())
                .email(email)
                .authorities(authorities)
                .token(String.format("Bearer %s", token))
                .methodType(HttpMethod.GET.name())
                .isAuthenticated(true)
                .build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(respModel));
    }
}
