package by.salary.authorizationserver.model.oauth2;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleAuthentication implements AuthenticationUserInfo {

    Map<String, Object> attributes;

    @Override
    public String getId() {
        return (String)attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getUri() {
        return (String)attributes.get("picture");
    }

    @Override
    public String getNameAttribureKey() {
        return "sub";
    }

    @Override
    public AuthenticationRegistrationId getAuthenticationAttributeKey() {
        return AuthenticationRegistrationId.google;
    }
}
