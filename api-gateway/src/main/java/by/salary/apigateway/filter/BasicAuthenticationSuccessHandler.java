package by.salary.apigateway.filter;

import by.salary.apigateway.util.SecurityConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BasicAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        response.setHeader(SecurityConstants.HEADER, SecurityConstants.PREFIX + authentication.getCredentials());

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(frontendUrl);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
