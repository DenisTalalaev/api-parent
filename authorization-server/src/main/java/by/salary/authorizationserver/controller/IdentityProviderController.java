package by.salary.authorizationserver.controller;

import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthDto;
import by.salary.authorizationserver.model.dto.RegisterDto;
import by.salary.authorizationserver.model.dto.RegisterRequest;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.service.AuthorizationService;
import by.salary.authorizationserver.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public String register(@RequestBody RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        authorizationService.save(mapToRegisterDto(registerRequest));
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

    private RegisterDto mapToRegisterDto(RegisterRequest registerRequest){
        return RegisterDto.builder()
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .authorities(List.of("USER"))
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .build();

    }

}
