package by.salary.authorizationserver.filter;

import by.salary.authorizationserver.model.JwtAuthenticationToken;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.util.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    JwtService service;
    AuthorizationService authorizationService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        String jwt = (String) jwtAuthenticationToken.getCredentials();

        String email = service.extractUserName(jwt);
        Optional<UserInfoDTO> userInfoDTO = authorizationService.findByEmail(email);
        if (userInfoDTO.isPresent()) {
            UserInfoDTO user = userInfoDTO.get();
            if (service.isTokenValid(jwt, user)) {
                Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities();

                if(grantedAuthorities.equals(jwtAuthenticationToken.getAuthorities())){
                    return new UsernamePasswordAuthenticationToken(email, null, grantedAuthorities);
                }
            }
        }

        throw new AuthenticationCredentialsNotFoundException("Jwt token is not valid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
