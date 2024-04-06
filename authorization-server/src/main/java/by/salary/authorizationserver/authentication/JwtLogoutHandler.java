package by.salary.authorizationserver.authentication;

import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.authentication.token.UsernameEmailPasswordAuthenticationToken;
import by.salary.authorizationserver.repository.TokenRepository;
import by.salary.authorizationserver.service.JwtService;
import by.salary.authorizationserver.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Transactional
public class JwtLogoutHandler implements LogoutHandler {
    TokenService tokenService;
    JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String username;
        if (authentication instanceof UsernameEmailPasswordAuthenticationToken){
            username = ((UsernameEmailPasswordAuthenticationToken) authentication).getUsername();
        }else if (authentication instanceof UsernamePasswordAuthenticationToken){
            username = authentication.getName();
        }else if (authentication instanceof JwtAuthenticationToken){
            String token = (String) authentication.getPrincipal();
            username = jwtService.extractUserName(token);
        }else {
            throw new UnsupportedOperationException("Unsupported authentication mechanism. Cannot logout");
        }
        tokenService.deleteAllByUsername(username);
    }
}
