package by.salary.serviceuser.filter.authenticationUtils;

import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@AllArgsConstructor
public class PermissionAccessFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String email = (String) request.getAttribute("email");
        if(email == null){
            filterChain.doFilter(request, response);
            return;
        }
        Optional<User> optUser = userRepository.findByUserEmail(email);
        if(optUser.isEmpty()){
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User user = optUser.get();
        request.setAttribute("permissions", user.getPermissions());
        filterChain.doFilter(request, response);
    }



}
