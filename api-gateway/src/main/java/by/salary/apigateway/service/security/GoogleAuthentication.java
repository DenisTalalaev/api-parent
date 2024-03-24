package by.salary.apigateway.service.security;

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
}
