package by.salary.authorizationserver.controller;

import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.dto.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.dto.RegisterLocalUserRequest;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.AuthenticationRegistrationService;
import by.salary.authorizationserver.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class IdentityProviderController {

    AuthorizationRepository authorizationRepository;

    AuthenticationRegistrationService authenticationRegistrationService;

    JwtService jwtService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDto register(@RequestBody RegisterLocalUserRequest registerLocalUserRequest) {
        return authenticationRegistrationService.register(registerLocalUserRequest);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ConnValidationResponse token(@RequestBody AuthenticationLocalUserRequest authDto) {
        return authenticationRegistrationService.authenticate(authDto);
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
                .authorities(authorities.stream().map((ga) -> new Authority(ga.getAuthority())).toList())
                .build();
    }

}
