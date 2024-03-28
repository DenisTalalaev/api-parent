package by.salary.authorizationserver.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class AuthenticationResponseDto {

    private boolean isEnabled;
    private boolean isAuthenticated;
    private String email;
    private List<String> authorities;

}
