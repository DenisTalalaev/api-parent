package by.salary.authorizationserver.authentication;

import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.authentication.token.UsernameEmailPasswordAuthenticationToken;
import by.salary.authorizationserver.repository.TokenRepository;
import by.salary.authorizationserver.service.JwtService;
import by.salary.authorizationserver.service.TokenService;
import by.salary.authorizationserver.util.AuthenticationUtils;
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
    AuthenticationUtils authenticationUtils;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String username = authenticationUtils.extractUsername(authentication);

        tokenService.deleteAllByUsername(username);
    }
}
