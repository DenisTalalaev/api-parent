package by.salary.authorizationserver.authentication;

import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.entity.MailType;
import by.salary.authorizationserver.model.oauth2.AuthenticationUserInfo;
import by.salary.authorizationserver.model.oauth2.FactoryAuthenticationRegistration;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.JwtService;
import by.salary.authorizationserver.service.TokenRegistrationService;
import by.salary.authorizationserver.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String frontendUrl;
    private final AuthorizationRepository authorizationRepository;

    TokenRegistrationService tokenRegistrationService;
    @Autowired
    public OAuth2AuthenticationSuccessHandler(@Value("${frontend.url}") String frontendUrl,
                                              AuthorizationRepository authorizationRepository,
                                              TokenRegistrationService tokenRegistrationService) {
        this.authorizationRepository = authorizationRepository;
        this.frontendUrl = frontendUrl;
        this.tokenRegistrationService = tokenRegistrationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        //token from oauth2 resource
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String authorizationId = token.getAuthorizedClientRegistrationId();
        AuthenticationUserInfo authUserInfo = FactoryAuthenticationRegistration.create(
                authorizationId,
                attributes
        );

        AuthenticationRequestDto authenticationRequestDto = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(authUserInfo.getAuthenticationAttributeKey())
                .authorizationRegistrationKey(authUserInfo.getId())
                .userEmail(authUserInfo.getEmail())
                .build();

        Optional<AuthenticationResponseDto> responseDto = authorizationRepository.find(authenticationRequestDto);
        String jwt;
        //if user already exists
        if (responseDto.isPresent()) {
            jwt = authenticatedUser(responseDto.get(), token, authUserInfo, attributes);
        //registration
        }else {
            //creating request for registration
            RegisterRequestDto registerRequestDto = mapToRegisterDto(authUserInfo);

            //saving user
            RegisterResponseDto registerResponseDto = authorizationRepository.save(registerRequestDto);

            //if user registered
            if (registerResponseDto.getHttpStatus().is2xxSuccessful()){
                Optional<AuthenticationResponseDto> afterSaveResponseDto = authorizationRepository.find(authenticationRequestDto);
                //if user found
                if (afterSaveResponseDto.isPresent()){
                    jwt = authenticatedUser(afterSaveResponseDto.get(), token, authUserInfo, attributes);
                } else {
                    throw new RuntimeException("Internal server error. User registered successfully but not found.");
                }
            }else {
                throw new RuntimeException(registerResponseDto.getMessage());
            }
        }

        //sending token to frontend
        URI uri = UriComponentsBuilder.fromHttpUrl(frontendUrl).queryParam("token", jwt).build().toUri();
        String url = uri.toString();
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(url);
        this.setRedirectStrategy(new DefaultRedirectStrategy(){
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
                response.setHeader("Authorization", "Bearer " + jwt);
                super.sendRedirect(request, response, url);
            }
        });
        super.onAuthenticationSuccess(request, response, authentication);
    }

    String authenticatedUser(AuthenticationResponseDto user,
                           OAuth2AuthenticationToken token,
                           AuthenticationUserInfo authUserInfo,
                           Map<String, Object> attributes) {
            Collection<? extends GrantedAuthority> roles = user.getAuthorities().stream().map(
                    (a) -> new SimpleGrantedAuthority(a.getAuthority())
            ).collect(Collectors.toList());
            DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                    roles,
                    attributes,
                    authUserInfo.getNameAttribureKey()
            );
            Authentication securityAuthentication = new OAuth2AuthenticationToken(
                    defaultOAuth2User, roles, token.getAuthorizedClientRegistrationId()
            );
            SecurityContextHolder.getContext().setAuthentication(securityAuthentication);

            return tokenRegistrationService.generateToken(mapToUserInfoDto(user), MailType._2FA);
    }

    RegisterRequestDto mapToRegisterDto(AuthenticationUserInfo authUserInfo){
        return RegisterRequestDto.builder()
                .authenticationRegistrationId(authUserInfo.getAuthenticationAttributeKey())
                .authenticationRegistrationKey(authUserInfo.getId())
                .userEmail(authUserInfo.getEmail())
                .pictureUri(authUserInfo.getUri())
                .authenticationRegistrationId(authUserInfo.getAuthenticationAttributeKey())
                .is2FEnabled(true)
                .build();
    }

    private UserInfoDTO mapToUserInfoDto(AuthenticationResponseDto authenticationResponseDto) {
        return UserInfoDTO.builder()
                .name(authenticationResponseDto.getUserName())
                .email(authenticationResponseDto.getUserEmail())
                .authorities(
                        authenticationResponseDto.getAuthorities()
                                .stream().map(Authority::getAuthority)
                        .collect(Collectors.toList())
                )
                .is2FEnabled(true)
                .is2FVerified(true)
                .build();
    }
}
