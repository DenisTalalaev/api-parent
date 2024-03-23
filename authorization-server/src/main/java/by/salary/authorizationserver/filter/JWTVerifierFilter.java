package by.salary.authorizationserver.filter;

import by.salary.authorizationserver.model.JwtAuthenticationToken;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.util.JwtService;
import by.salary.authorizationserver.util.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JWTVerifierFilter extends OncePerRequestFilter {
    JwtService service;
    AuthorizationService authorizationService;

    AuthenticationManager authenticationManager;


    /**
     * Filter that authenticates the user and adds the user to the security context
     * Also verifies the JWT token from the request and adds it to the security context and as request attributes
     * @param request -
     * @param response -
     * @param filterChain -
     * @throws ServletException
     * @throws IOException
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

        Authentication auth = authenticationManager.authenticate(new JwtAuthenticationToken(authToken, service));

        SecurityContextHolder.getContext().setAuthentication(auth);

        request.setAttribute("email", service.extractUserName(authToken));
        request.setAttribute("authorities", service.extractAuthorities(authToken));
        request.setAttribute("jwt", authToken);

        filterChain.doFilter(request, response);
    }
}
