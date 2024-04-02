package by.salary.authorizationserver.authentication.token;

import by.salary.authorizationserver.util.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String jwt;
    public JwtAuthenticationToken(String jwt, JwtService jwtService) {
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
