package by.salary.apigateway.service.security;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GithubAuthentication implements AuthenticationUserInfo {
    Map<String, Object> attributes;

    @Override
    public String getId() {
        return ((Integer) attributes.getOrDefault("id", null)).toString();
    }

    @Override
    public String getEmail() {
        return (String) attributes.getOrDefault("email", "");
    }


    @Override
    public String getUri() {
        return (String) attributes.getOrDefault("avatar_url", "");
    }

    @Override
    public String getNameAttribureKey() {
        return "id";
    }
}
