package by.salary.authorizationserver.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserService implements UserDetailsService {
    AuthorizationService authorizationService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authorizationService.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }
}
