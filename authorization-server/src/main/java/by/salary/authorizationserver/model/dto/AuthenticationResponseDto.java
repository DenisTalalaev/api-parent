package by.salary.authorizationserver.model.dto;

import by.salary.authorizationserver.model.entity.Authority;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
public class AuthenticationResponseDto {

    private String userName;

    private String userEmail;

    private String password;

    Collection<Authority> authorities;

}
