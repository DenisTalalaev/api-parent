package by.salary.authorizationserver.authentication.token;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class UsernameEmailPasswordAuthenticationToken extends AbstractAuthenticationToken {

    private String username;
    private String userEmail;
    private String password;
    private boolean is2FEnabled;
    private boolean is2FVerified;

    public UsernameEmailPasswordAuthenticationToken(String username, String email, String password) {
        super(null);
        this.username = username;
        this.userEmail = email;
        this.password = password;
    }

    public UsernameEmailPasswordAuthenticationToken(String username, String email, String password,
                                                    boolean is2FEnabled, boolean is2FVerified,
                                                    Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.userEmail = email;
        this.password = password;
        this.is2FEnabled = is2FEnabled;
        this.is2FVerified = is2FVerified;
    }

    @Override
    public Object getCredentials() {
        return UsernameEmailEntity.builder()
                .username(username)
                .userEmail(userEmail)
                .build();
    }

    @Override
    public Object getPrincipal() {
        return password;
    }

    @Builder
    @Getter
    @Setter
    public static class UsernameEmailEntity{
        private String username;
        private String userEmail;
    }
}
