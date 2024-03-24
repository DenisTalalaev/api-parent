package by.salary.apigateway.service.security;

import by.salary.apigateway.model.AuthenticationDto;
import by.salary.apigateway.model.ConnValidationResponse;
import by.salary.apigateway.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@AllArgsConstructor
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    AuthenticationService authenticationService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        AuthenticationDto authenticationDto = new AuthenticationDto(email, password);

        ConnValidationResponse connValidationResponse = authenticationService.authenticate(authenticationDto);

        return new UsernamePasswordAuthenticationToken(connValidationResponse.getEmail(), connValidationResponse.getToken(),
                connValidationResponse.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
