package by.salary.authorizationserver.controller;

import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.RegisterRequest;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.service.RegistrationService;
import by.salary.authorizationserver.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class IdentityProviderController {

    AuthorizationService authorizationService;

    RegistrationService registrationService;

    JwtService jwtService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDto register(@RequestBody RegisterRequest registerRequest) {
        return registrationService.register(registerRequest);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ConnValidationResponse token(@RequestBody AuthenticationRequestDto authDto) {
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
