package by.salary.authorizationserver.authentication.filter;

import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.util.JwtService;
import by.salary.authorizationserver.util.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class JWTVerifierFilter extends OncePerRequestFilter {
    JwtService service;
    AuthorizationRepository authorizationRepository;

    AuthenticationManager authenticationManager;


    /**
     * Filter that authenticates the user and adds the user to the security context
     * Also verifies the JWT token from the request and adds it to the security context and as request attributes
     * @param request -
     * @param response -
     * @param filterChain -
     * @throws ServletException doesn't throw
     * @throws IOException doesn't throw
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader(SecurityConstants.HEADER);
        if (!(!StringUtils.isEmpty(bearerToken) && bearerToken.startsWith(SecurityConstants.PREFIX))) {
            filterChain.doFilter(request, response);
            return;
        }
        String authToken = bearerToken.replace(SecurityConstants.PREFIX, "");

        try {
            Authentication auth = authenticationManager.authenticate(new JwtAuthenticationToken(authToken, service));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }catch (AuthenticationException e){
            log.error("Jwt Authentication failed {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        request.setAttribute("email", service.extractEmail(authToken));
        request.setAttribute("authorities", service.extractAuthorities(authToken));
        request.setAttribute("jwt", authToken);

        filterChain.doFilter(request, response);
    }
}
