package by.salary.apigateway.filter;

import by.salary.apigateway.model.AuthenticationDto;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@AllArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    ObjectMapper objectMapper;
    AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            AuthenticationDto authenticationDto = objectMapper.readValue(request.getInputStream(),
                    AuthenticationDto.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(),
                    authenticationDto.getPassword()));
        }catch (AuthenticationException e) {
            throw new UsernameNotFoundException("Could not found user credentials in request", e);
        }catch (StreamReadException | DatabindException e) {
            throw new AuthenticationServiceException("Unsupported format", e);
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException("Could not found user credentials in request", e);
        }

    }
}
