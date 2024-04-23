package by.salary.authorizationserver.service;

import by.salary.authorizationserver.exception.EmailNotFoundException;
import by.salary.authorizationserver.exception.UserNotFoundException;
import by.salary.authorizationserver.model.ConnValidationResponse;
import by.salary.authorizationserver.model.RestError;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.*;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.model.userrequest.AuthenticationLocalUserRequest;
import by.salary.authorizationserver.model.userrequest.RegisterLocalUserRequest;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import by.salary.authorizationserver.util.AuthenticationUtils;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class AuthenticationRegistrationService {

    JwtService jwtService;

    AuthorizationRepository authorizationRepository;

    PasswordEncoder passwordEncoder;

    TokenRegistrationService tokenRegistrationService;

    MailService mailService;

    AuthenticationUtils authenticationUtils;

    LogoutHandler logoutHandler;

    String frontendUrl;

    @Autowired
    public AuthenticationRegistrationService(JwtService jwtService,
                                             AuthorizationRepository authorizationRepository,
                                             PasswordEncoder passwordEncoder,
                                             TokenRegistrationService tokenRegistrationService,
                                             MailService mailService,
                                             AuthenticationUtils authenticationUtils,
                                             LogoutHandler logoutHandler,
                                             @Value("${frontend.redirect.url}") String frontendUrl) {
        this.jwtService = jwtService;
        this.authorizationRepository = authorizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRegistrationService = tokenRegistrationService;
        this.mailService = mailService;
        this.authenticationUtils = authenticationUtils;
        this.logoutHandler = logoutHandler;
        this.frontendUrl = frontendUrl;
    }

    public RegisterResponseDto register(RegisterLocalUserRequest registerLocalUserRequest) {

        MailRequestDTO mailRequestDTO = MailRequestDTO.builder()
                .mailTo(registerLocalUserRequest.getUserEmail())
                .build();

        if (!mailService.checkEmail(mailRequestDTO)){
            throw new EmailNotFoundException("Email " + registerLocalUserRequest.getUserEmail() + " not found");
        }

        registerLocalUserRequest.setPassword(passwordEncoder.encode(registerLocalUserRequest.getPassword()));
        return authorizationRepository.save(mapToRegisterDto(registerLocalUserRequest));
    }


    public ConnValidationResponse authenticate(AuthenticationLocalUserRequest authenticationRequestDto) throws UserNotFoundException {
        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .username(authenticationRequestDto.getUsername())
                .userEmail(authenticationRequestDto.getUserEmail())
                .build();

        Optional<AuthenticationResponseDto> authentication = authorizationRepository.find(authenticationRequest);

        if (authentication.isEmpty()){
            throw new UserNotFoundException("Authentication failed");
        }
        //TODO: verify email

        if (!passwordEncoder.matches(authenticationRequestDto.getPassword(), authentication.get().getPassword())){
            //TODO: exception handling
            throw new UserNotFoundException("Authentication failed");
        }

        String token = tokenRegistrationService.generateToken(mapToUserInfoDto(authentication.get()));

        return mapToConnValidationResponse(authentication.get(), token);
    }

    public AuthenticationChangePasswordResponseDto changePassword(String email, ChangePasswordRequestDto changePasswordRequestDto) {
        AuthenticationChangePasswordRequestDto requestDto = AuthenticationChangePasswordRequestDto.builder()
                .email(email)
                .password(passwordEncoder.encode(changePasswordRequestDto.getPassword()))
                .build();

        return authorizationRepository.changePassword(requestDto);
    }

    public ResponseEntity<?> forgotPassword(ForgetPasswordRequestDto forgotPasswordRequestDto) {

        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .userEmail(forgotPasswordRequestDto.getEmail())
                .build();

        Optional<AuthenticationResponseDto> responseDto = authorizationRepository.find(authenticationRequest);

        if (responseDto.isEmpty()){
            RestError error = RestError.builder().httpStatus(HttpStatus.NOT_FOUND).message("User not found").build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        /*if (!responseDto.get().is2FEnabled()){
            RestError error = RestError.builder().httpStatus(HttpStatus.BAD_REQUEST).message("2FA is not enabled").build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }*/
        List<String> authorities = List.of("CHANGE_PASSWORD");
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .email(forgotPasswordRequestDto.getEmail())
                .name(responseDto.get().getUserName())
                .authorities(authorities)
                .is2FEnabled(true)
                .is2FVerified(false)
                .build();

        String token = tokenRegistrationService.generateToken(userInfoDTO);
        ConnValidationResponse response = ConnValidationResponse.builder()
                .token(token)
                .is2FEnabled(true)
                .is2FVerified(false)
                .authorities(authorities)
                .methodType("Bearer")
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> changeEmail(ChangeEmailRequestDto changeEmailRequestDto,
                                         String oldEmail, String jwt) {

        //TODO: generate update token

        //check if user want to change to email that already exists
        if (!changeEmailRequestDto.getEmail().equals(oldEmail)){
            AuthenticationRequestDto newEmailCheck = AuthenticationRequestDto.builder()
                    .authenticationRegistrationId(AuthenticationRegistrationId.local)
                    .userEmail(changeEmailRequestDto.getEmail())
                    .build();

            Optional<AuthenticationResponseDto> newEmailResponseDto = authorizationRepository.find(newEmailCheck);

            //if user want to change to email that already exists
            if (newEmailResponseDto.isPresent()){
                RestError error = RestError.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Email already exists").build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }

        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .userEmail(oldEmail)
                .build();

        Optional<AuthenticationResponseDto> responseDto = authorizationRepository.find(authenticationRequest);


        if (responseDto.isEmpty()){
            RestError error = RestError.builder().httpStatus(HttpStatus.NOT_FOUND).message("User not found").build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .name(responseDto.get().getUserName())
                .email(responseDto.get().getUserEmail())
                .authorities(List.of("CHANGE_EMAIL=" + changeEmailRequestDto.getEmail(),
                        "IS2F_ENABLED=" + changeEmailRequestDto.is2FEnabled()))
                .build();

        String token = tokenRegistrationService.generateToken(userInfoDTO);

        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(frontendUrl + "/verifyEmail").queryParam("token", token);

        //TODO: send to old old email
        MailRequestDTO mailRequestDTO = MailRequestDTO.builder()
                        .message(uriBuilder.toUriString())
                        .mailTo(changeEmailRequestDto.getEmail())
                        .build();

        mailService.sendMessage(mailRequestDTO, jwt);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<?> changeEmail(String jwt,
                                         List<GrantedAuthority> authorities) {

        String changeEmailAuthority = null;
        String hasIs2FEnabledAuthority = null;
        for(GrantedAuthority authority : authorities) {
            if(authority.getAuthority().contains("CHANGE_EMAIL")){
               changeEmailAuthority = authority.getAuthority();
            }
            if(authority.getAuthority().contains("IS2F_ENABLED")){
                hasIs2FEnabledAuthority = authority.getAuthority();
            }
        }

        if (changeEmailAuthority == null && hasIs2FEnabledAuthority == null){
            RestError error = RestError.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Incorrect token authorities").build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }



        String email = extractEmail(changeEmailAuthority);
        Boolean is2FEnabled = extractIs2FEnabled(hasIs2FEnabledAuthority);
        ChangeVerifiedEmailRequestDto changeVerifiedEmailRequestDto = ChangeVerifiedEmailRequestDto.builder()
                .email(email)
                .is2FEnabled(is2FEnabled)
                .build();

        ChangeEmailResponseDto changeEmailResponseDto = authorizationRepository.changeEmail(changeVerifiedEmailRequestDto, jwt);


        return ResponseEntity.status(changeEmailResponseDto.getStatus()).body(changeEmailResponseDto);
    }

    private String extractEmail(String changeEmailAuthorizationCode) {
        return changeEmailAuthorizationCode.replace("CHANGE_EMAIL=", "");
    }

    private Boolean extractIs2FEnabled(String has2FAuthoruty) {
        String is2FEnabled = has2FAuthoruty.replace("IS2F_ENABLED=", "");
        return is2FEnabled.equals("true");
    }

    private RegisterRequestDto mapToRegisterDto(RegisterLocalUserRequest registerLocalUserRequest){
        return RegisterRequestDto.builder()
                .username(registerLocalUserRequest.getUsername())
                .userEmail(registerLocalUserRequest.getUserEmail())
                .userPassword(registerLocalUserRequest.getPassword())
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .is2FEnabled(registerLocalUserRequest.is2FEnabled())
                .build();
    }


    private ConnValidationResponse mapToConnValidationResponse(AuthenticationResponseDto authentication, String token) {
        return ConnValidationResponse.builder()
                .status(HttpStatus.OK)
                .isAuthenticated(true)
                .methodType("Bearer")
                .email(authentication.getUserEmail())
                .token(token)
                .authorities(authentication.getAuthorities().stream().map(Authority::getAuthority).toList())
                .build();
    }

    private UserInfoDTO mapToUserInfoDto(AuthenticationResponseDto authenticationResponseDto){
        return UserInfoDTO.builder()
                .name(authenticationResponseDto.getUserName())
                .email(authenticationResponseDto.getUserEmail())
                .authorities(authenticationResponseDto.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toList()))
                .is2FEnabled(authenticationResponseDto.is2FEnabled())
                .is2FVerified(false)
                .build();

    }
}
