package by.salary.authorizationserver.authentication.filter;

import by.salary.authorizationserver.authentication.VerificationCodeHandler;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.TokenEntity;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.authentication.token.UsernameEmailPasswordAuthenticationToken;
import by.salary.authorizationserver.model.entity.MailType;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.service.JwtService;
import by.salary.authorizationserver.service.TokenRegistrationService;
import by.salary.authorizationserver.service.TokenService;
import by.salary.authorizationserver.util.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;

    TokenRegistrationService tokenRegistrationService;

    public JWTAuthenticationFilter(ObjectMapper objectMapper,
                                   AuthenticationManager authenticationManager,
                                   TokenRegistrationService tokenRegistrationService){
        setFilterProcessesUrl("/auth/token");
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.tokenRegistrationService = tokenRegistrationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            AuthenticationLocalUserRequest authDto = objectMapper.readValue(request.getInputStream(),
                    AuthenticationLocalUserRequest.class);
            return authenticationManager.authenticate(new UsernameEmailPasswordAuthenticationToken(authDto.getUsername(),
                    authDto.getUserEmail(),
                    authDto.getPassword()));
        } catch (IOException e) {
            log.error("Jwt Authentication failed in {}, {}", this.getClass(), e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("Could not found user", e);
        }
    }


    // this is called when user is successfully authenticated in auth/token
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UsernameEmailPasswordAuthenticationToken authToken = (UsernameEmailPasswordAuthenticationToken) authResult;

        //If user already have token then regenerate it and modify
        UserInfoDTO userInfo = mapToUserDetails(authToken);


        String token = tokenRegistrationService.generateToken(userInfo, MailType._2FA);


        //preparing response
        response.addHeader(SecurityConstants.HEADER, String.format("Bearer %s", token));


        ConnValidationResponse respModel = mapToConnValidationResponse(authToken, token);

        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(respModel));
    }

    private UserInfoDTO mapToUserDetails(UsernameEmailPasswordAuthenticationToken authentication) {
        return UserInfoDTO.builder()
                .name(authentication.getUsername())
                .email(authentication.getUserEmail())
                .is2FEnabled(authentication.is2FEnabled())
                .is2FVerified(false)
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .build();
    }

    private ConnValidationResponse mapToConnValidationResponse(UsernameEmailPasswordAuthenticationToken authentication,
                                                               String jwt) {

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .email(authentication.getUserEmail())
                .authorities(authorities)
                .token(jwt)
                .methodType(HttpMethod.GET.name())
                .isAuthenticated(true)
                .is2FEnabled(authentication.is2FEnabled())
                .is2FVerified(authentication.is2FVerified())
                .build();
    }
}
