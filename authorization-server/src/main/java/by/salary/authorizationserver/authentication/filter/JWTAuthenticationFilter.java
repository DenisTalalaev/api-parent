package by.salary.authorizationserver.authentication.filter;

import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.authentication.token.UsernameEmailPasswordAuthenticationToken;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.util.JwtService;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;
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

        String token = jwtService.generateToken(mapToUserDetails(authToken));

        //TODO: save token in database

        response.addHeader(SecurityConstants.HEADER, String.format("Bearer %s", token));

        ConnValidationResponse respModel = mapToConnValidationResponse(authToken, token);

        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(respModel));
    }

    private UserInfoDTO mapToUserDetails(UsernameEmailPasswordAuthenticationToken authentication) {
        return UserInfoDTO.builder()
                .name(authentication.getUsername())
                .email(authentication.getUserEmail())
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .build();
    }

    private ConnValidationResponse mapToConnValidationResponse(UsernameEmailPasswordAuthenticationToken authentication,
                                                               String jwt) {

        List<Authority> authorities = authentication.getAuthorities().stream()
                .map((a) -> new Authority(a.getAuthority())).toList();
        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .email(authentication.getUserEmail())
                .authorities(authorities)
                .token(String.format("Bearer %s", jwt))
                .methodType(HttpMethod.GET.name())
                .isAuthenticated(true)
                .build();
    }
}
