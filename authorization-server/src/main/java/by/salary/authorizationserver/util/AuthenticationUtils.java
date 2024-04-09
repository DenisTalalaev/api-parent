package by.salary.authorizationserver.util;

import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.authentication.token.UsernameEmailPasswordAuthenticationToken;
import by.salary.authorizationserver.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

    JwtService jwtService;

    public String extractUsername(Authentication authentication) {
        if (authentication instanceof UsernameEmailPasswordAuthenticationToken){
            return ((UsernameEmailPasswordAuthenticationToken) authentication).getUsername();
        }else if (authentication instanceof UsernamePasswordAuthenticationToken){
            return authentication.getName();
        }else if (authentication instanceof JwtAuthenticationToken){
            String token = (String) authentication.getPrincipal();
            return jwtService.extractUserName(token);
        }else {
            throw new UnsupportedOperationException("Unsupported authentication mechanism. Cannot logout");
        }
    }
}
