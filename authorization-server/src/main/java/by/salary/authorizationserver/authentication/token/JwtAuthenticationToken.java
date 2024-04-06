package by.salary.authorizationserver.authentication.token;

import by.salary.authorizationserver.service.JwtService;
import org.springframework.security.authentication.AbstractAuthenticationToken;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String jwt;
    public JwtAuthenticationToken(String jwt, JwtService jwtService) throws Exception {
        super(jwtService.extractAuthorities(jwt));
        this.jwt = jwt;
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
