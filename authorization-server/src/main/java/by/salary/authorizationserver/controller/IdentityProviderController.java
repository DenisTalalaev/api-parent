package by.salary.authorizationserver.controller;

import by.salary.authorizationserver.authentication.JwtLogoutHandler;
import by.salary.authorizationserver.authentication.VerificationCodeHandler;
import by.salary.authorizationserver.authentication.token.JwtAuthenticationToken;
import by.salary.authorizationserver.exception.UserNotFoundException;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.RestError;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthenticationChangePasswordResponseDto;
import by.salary.authorizationserver.model.dto.ChangePasswordRequestDto;
import by.salary.authorizationserver.model.dto.ForgetPasswordRequestDto;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.userrequest.RegisterLocalUserRequest;
import by.salary.authorizationserver.model.dto.RegisterResponseDto;
import by.salary.authorizationserver.model.userrequest.VerificationCodeRequest;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.service.AuthenticationRegistrationService;
import by.salary.authorizationserver.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class IdentityProviderController {

    AuthorizationRepository authorizationRepository;

    AuthenticationRegistrationService authenticationRegistrationService;

    JwtService jwtService;
    JwtLogoutHandler logoutHandler;
    VerificationCodeHandler verificationCodeHandler;


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
        boolean is2FEnabled = (boolean) request.getAttribute("is2FEnabled");
        boolean is2FVerified = (boolean) request.getAttribute("is2FVerified");

        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .isAuthenticated(true)
                .methodType("Bearer")
                .email(email)
                .token(jwt)
                .authorities(authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .is2FEnabled(is2FEnabled)
                .is2FVerified(is2FVerified)
                .build();
    }

    @PostMapping(value = "/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logoutHandler.logout(request, response, authentication);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/verify/code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationCodeRequest code, HttpServletRequest request) {

        String email = (String) request.getAttribute("email");
        String jwt = (String) request.getAttribute("jwt");
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) request.getAttribute("authorities");
        boolean is2FEnabled = (boolean) request.getAttribute("is2FEnabled");
        boolean is2FVerified = (boolean) request.getAttribute("is2FVerified");
        //verify code
        boolean isVerified = verificationCodeHandler.verify(jwt, code.getCode());

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .name(jwtService.extractUserName(jwt))
                .email(email)
                .authorities(authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .is2FEnabled(is2FEnabled)
                .is2FVerified(isVerified)
                .build();

        //generate new token with isVerified = true
        if (isVerified) {
            ConnValidationResponse connValidationResponse = verificationCodeHandler.generateNewToken(userInfoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(connValidationResponse);
        }

        RestError errorMessage = RestError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message("Incorrect validation code")
                .build();
        return ResponseEntity.status(errorMessage.getHttpStatus()).body(errorMessage);
    }

    @PostMapping(value = "/forgot/password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordRequestDto requestDto) {

        return authenticationRegistrationService.forgotPassword(requestDto);
    }

    @PutMapping(value = "/change/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String jwt = (String) request.getAttribute("jwt");
        String email = (String) request.getAttribute("email");

        if (!verificationCodeHandler.verify(jwt, changePasswordRequestDto.getVerificationCode())){
            RestError error = RestError.builder().httpStatus(HttpStatus.UNAUTHORIZED).message("Incorrect validation code")
                    .build();
            return ResponseEntity.status(error.getHttpStatus()).body(error);
        }

        AuthenticationChangePasswordResponseDto responseDto =  authenticationRegistrationService.changePassword(email,
                changePasswordRequestDto);

        if (responseDto.getStatus().is2xxSuccessful()){
            logoutHandler.logout(request, response, authentication);
        }

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

}
