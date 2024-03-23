package by.salary.authorizationserver.controller;

import by.salary.authorizationserver.exception.UserAlreadyExistsException;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthDto;
import by.salary.authorizationserver.model.dto.RegisterDto;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class IdentityProviderController {

    AuthorizationService authorizationService;

    JwtService jwtService;

    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@RequestBody RegisterDto registerDto) throws UserAlreadyExistsException {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        authorizationService.save(registerDto);
        return "User created";
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ConnValidationResponse token(@RequestBody AuthDto authDto) {
        var optional = authorizationService.findByEmail(authDto.getEmail());
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        var userInfoDTO = optional.get();

        return mapToConnValidationResponse(userInfoDTO);
    }

    @GetMapping(value = "/valid")
    @ResponseStatus(HttpStatus.OK)
    public ConnValidationResponse valid(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String jwt = (String) request.getAttribute("jwt");
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) request.getAttribute("authorities");

        return ConnValidationResponse.builder()
                .status("OK")
                .isAuthenticated(true)
                .methodType("Bearer")
                .email(email)
                .token(jwt)
                .authorities(authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    private ConnValidationResponse mapToConnValidationResponse(UserInfoDTO userInfoDTO) {
        return mapToConnValidationResponse(userInfoDTO, jwtService.generateToken(userInfoDTO));

    }

    private ConnValidationResponse mapToConnValidationResponse(UserInfoDTO userInfoDTO, String token) {
        return ConnValidationResponse.builder()
                .status("OK")
                .isAuthenticated(true)
                .methodType("Bearer")
                .email(userInfoDTO.getEmail())
                .token(token)
                .authorities(userInfoDTO.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

}
