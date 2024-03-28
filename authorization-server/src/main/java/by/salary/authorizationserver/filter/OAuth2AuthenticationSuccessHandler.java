package by.salary.authorizationserver.filter;

import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import by.salary.authorizationserver.model.oauth2.AuthenticationUserInfo;
import by.salary.authorizationserver.model.oauth2.FactoryAuthenticationRegistration;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.util.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String frontendUrl;
    private final AuthorizationService authorizationService;
    private final JwtService jwtService;
    @Autowired
    public OAuth2AuthenticationSuccessHandler(AuthorizationService authorizationService,
                                              JwtService jwtService) {
        this.authorizationService = authorizationService;
        this.jwtService = jwtService;
        this.frontendUrl = "http://localhost:8080";
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String authorizationId = token.getAuthorizedClientRegistrationId();
        AuthenticationUserInfo authUserInfo = FactoryAuthenticationRegistration.create(
                authorizationId,
                attributes
        );

        Optional<UserInfoDTO> user = authorizationService.findByEmail(authUserInfo.getEmail());
        if (user.isPresent()) {
            authenticatedUser(response, token, authUserInfo, attributes).accept(user.get());
        }else {
            user = authorizationService.save(this.mapToRegisterDto(authUserInfo));
            user.ifPresent(userInfoDTO -> authenticatedUser(response, token, authUserInfo, attributes).accept(userInfoDTO));
        }

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        String jwt = jwtService.generateToken(user.get());

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(frontendUrl);
        this.setRedirectStrategy(new DefaultRedirectStrategy(){
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
                response.setHeader("Authorization", "Bearer " + jwt);
                super.sendRedirect(request, response, url);
            }
        });
        super.onAuthenticationSuccess(request, response, authentication);
    }

    Consumer<UserInfoDTO> authenticatedUser(HttpServletResponse response, OAuth2AuthenticationToken token,
                                            AuthenticationUserInfo authUserInfo,
                                            Map<String, Object> attributes) {
        return user -> {
            Collection<? extends GrantedAuthority> roles = user.getAuthorities();
            DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                    roles,
                    attributes,
                    authUserInfo.getNameAttribureKey()
            );
            Authentication securityAuthentication = new OAuth2AuthenticationToken(
                    defaultOAuth2User, roles, token.getAuthorizedClientRegistrationId()
            );
            SecurityContextHolder.getContext().setAuthentication(securityAuthentication);
            String jwt = jwtService.generateToken(user);
            response.setHeader("Authorization", "Bearer " + jwt);
        };
    }

    RegisterRequestDto mapToRegisterDto(AuthenticationUserInfo authUserInfo){
        return RegisterRequestDto.builder()
                .email(authUserInfo.getEmail())
                .pictureUri(authUserInfo.getUri())
                .authenticationRegistrationId(authUserInfo.getAuthenticationAttributeKey())
                .build();
    }
}
