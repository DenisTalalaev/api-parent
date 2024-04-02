package by.salary.authorizationserver.authentication.provider;

import by.salary.authorizationserver.exception.UserNotFoundException;
import by.salary.authorizationserver.authentication.token.UsernameEmailPasswordAuthenticationToken;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UsernameEmailPasswordAuthenticationProvider implements AuthenticationProvider {

    AuthorizationRepository authorizationRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernameEmailPasswordAuthenticationToken token = (UsernameEmailPasswordAuthenticationToken) authentication;

        AuthenticationRequestDto authenticationRequestDto = generateAuthenticationRequest(token);
        Optional<AuthenticationResponseDto> responseDto = authorizationRepository.find(authenticationRequestDto);

        if (responseDto.isEmpty()){
            throw new AuthenticationCredentialsNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(token.getPassword(), responseDto.get().getPassword())){
            //TODO: exception handling
            throw new UserNotFoundException("Authentication failed");
        }

        return mapToUserEmailPasswordAuthenticationToken(responseDto.get());
    }

    private UsernameEmailPasswordAuthenticationToken mapToUserEmailPasswordAuthenticationToken(AuthenticationResponseDto response) {

        return new UsernameEmailPasswordAuthenticationToken(
                response.getUserName(),
                response.getUserEmail(),
                response.getPassword(),
                response.getAuthorities().stream().map((a) -> new SimpleGrantedAuthority(a.getAuthority()))
                        .collect(Collectors.toList())
        );
    }

    private AuthenticationRequestDto generateAuthenticationRequest(UsernameEmailPasswordAuthenticationToken token) {

        return AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .userEmail(token.getUserEmail())
                .username(token.getUsername())
                .build();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernameEmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}