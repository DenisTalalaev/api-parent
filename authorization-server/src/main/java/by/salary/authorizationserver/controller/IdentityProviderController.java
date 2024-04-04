package by.salary.authorizationserver.controller;

import by.salary.authorizationserver.exception.UserNotFoundException;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.dto.RegisterRequestDto;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.userrequest.RegisterLocalUserRequest;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.AuthenticationRegistrationService;
import by.salary.authorizationserver.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
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
    public RegisterResponseDto register(@RequestBody RegisterLocalUserRequest registerLocalUserRequest,
                                        HttpServletResponse response) {
        RegisterResponseDto registerResponseDto = authenticationRegistrationService.register(registerLocalUserRequest);
        response.setStatus(registerResponseDto.getHttpStatus().value());
        return registerResponseDto;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ConnValidationResponse token(@RequestBody AuthenticationLocalUserRequest authDto, HttpServletResponse response)
            throws UserNotFoundException {
        ConnValidationResponse connValidationResponse = authenticationRegistrationService.authenticate(authDto);
        response.setStatus(connValidationResponse.getStatus().value());
        return connValidationResponse;
    }

    @GetMapping(value = "/valid")
    @ResponseStatus(HttpStatus.OK)
    public ConnValidationResponse valid(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String jwt = (String) request.getAttribute("jwt");
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) request.getAttribute("authorities");

        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .isAuthenticated(true)
                .methodType("Bearer")
                .email(email)
                .token(jwt)
                .authorities(authorities.stream().map((ga) -> new Authority(ga.getAuthority())).toList())
                .build();
    }

}
