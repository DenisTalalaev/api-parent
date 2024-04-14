package by.salary.authorizationserver.authentication.provider;

import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    JwtService service;
    AuthorizationRepository authorizationRepository;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        String jwt = (String) jwtAuthenticationToken.getCredentials();

        String userName = service.extractUserName(jwt);

        AuthenticationRequestDto authenticationRequestDto = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .username(userName)
                .userEmail(service.extractEmail(jwt))
                .build();

        Optional<AuthenticationResponseDto> responseDto = authorizationRepository.find(authenticationRequestDto);

        if (responseDto.isEmpty()){
            throw new AuthenticationCredentialsNotFoundException("Jwt token is not valid");
        }

        AuthenticationResponseDto response = responseDto.get();

        if (service.is2FEnabled(jwt) != response.is2FEnabled()) {
            throw new AuthenticationCredentialsNotFoundException("Jwt token is not valid: 2FA is not matched");
        }

        if (service.isTokenValid(jwt, mapToUserInfoDto(responseDto.get()))) {
            Collection<? extends GrantedAuthority> grantedAuthorities = responseDto.get().getAuthorities().stream().map((a) ->
                    new SimpleGrantedAuthority(a.getAuthority())).toList();

            if (grantedAuthorities.equals(jwtAuthenticationToken.getAuthorities())) {
                //return new JwtAuthenticationToken(jwt, service);
                return new UsernamePasswordAuthenticationToken(userName, null, grantedAuthorities);
            }
            throw new AuthenticationCredentialsNotFoundException("Jwt token is not valid: roles are not matched");
        }
        throw new AuthenticationCredentialsNotFoundException("Jwt token is not valid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private UserInfoDTO mapToUserInfoDto(AuthenticationResponseDto responseDto){

        return UserInfoDTO.builder()
                .name(responseDto.getUserName())
                .email(responseDto.getUserEmail())
                .build();
    }
}
