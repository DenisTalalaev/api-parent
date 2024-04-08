package by.salary.servicemail.authenticationUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.isEmpty(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        authHeader = authHeader.substring(7);

        String email = jwtService.extractEmail(authHeader);

        request.setAttribute("email", email);
        request.setAttribute("authorities", jwtService.extractAuthorities(authHeader));
        filterChain.doFilter(request, response);
    }
}
