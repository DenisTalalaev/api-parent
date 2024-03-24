package by.salary.apigateway.filter;

import by.salary.apigateway.model.UserInfoDTO;
import by.salary.apigateway.service.UserService;
import by.salary.apigateway.service.security.AuthenticationUserInfo;
import by.salary.apigateway.service.security.FactoryAuthenticationRegistration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    private final UserService userService;

    @Autowired
    OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Value("${frontend.url}")
    private String frontendUrl;

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

        userService.findByEmail(authUserInfo.getEmail()).ifPresentOrElse(
                user -> {
                    List<GrantedAuthority> roles = new ArrayList<>(user.getAuthorities());
                    DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                            roles,
                            attributes,
                            authUserInfo.getNameAttribureKey()
                    );
                    Authentication securityAuthentication = new OAuth2AuthenticationToken(
                            defaultOAuth2User, roles, token.getAuthorizedClientRegistrationId()
                    );
                    SecurityContextHolder.getContext().setAuthentication(securityAuthentication);
                }, () -> {
                    userService.save(this.mapToUserInfoDTO(authUserInfo));
                    List<GrantedAuthority> roles = List.of(mapToAuthority("USER"));
                    DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                            roles,
                            attributes,
                            authUserInfo.getNameAttribureKey()
                    );
                    Authentication securityAuthentication = new OAuth2AuthenticationToken(
                            defaultOAuth2User, roles, token.getAuthorizedClientRegistrationId()
                    );
                    SecurityContextHolder.getContext().setAuthentication(securityAuthentication);
                }
        );


        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(frontendUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    GrantedAuthority mapToAuthority(String role){

        return new SimpleGrantedAuthority(role);
    }

    UserInfoDTO mapToUserInfoDTO(AuthenticationUserInfo authUserInfo){
        return UserInfoDTO.builder()
                .email(authUserInfo.getEmail())
                .roles(List.of("USER"))
                .pictureUri(authUserInfo.getUri())
                .build();

    }
}
