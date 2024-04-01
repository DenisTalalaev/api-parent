package by.salary.servicetest.filter;

import by.salary.servicetest.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.isEmpty(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            throw new RuntimeException("Missing authorization header");
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing Bearer token");
        }

        authHeader = authHeader.substring(7);

        String email = jwtService.extractEmail(authHeader);

        request.setAttribute("email", email);
        request.setAttribute("authorities", jwtService.extractAuthorities(authHeader));
        filterChain.doFilter(request, response);
    }
}
