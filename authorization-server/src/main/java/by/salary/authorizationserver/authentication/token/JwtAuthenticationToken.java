package by.salary.authorizationserver.authentication.token;

import by.salary.authorizationserver.service.JwtService;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwt;

    private final String username;
    private final String email;
    private final boolean is2FEnabled;
    private final boolean is2FVerified;

    public JwtAuthenticationToken(String jwt, JwtService jwtService) throws RuntimeException {
        super(jwtService.extractAuthorities(jwt));
        this.jwt = jwt;
        this.username = jwtService.extractUserName(jwt);
        this.email = jwtService.extractEmail(jwt);
        this.is2FEnabled = jwtService.is2FEnabled(jwt);
        this.is2FVerified = jwtService.is2FVerified(jwt);
    }
    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return jwt;
    }
}
