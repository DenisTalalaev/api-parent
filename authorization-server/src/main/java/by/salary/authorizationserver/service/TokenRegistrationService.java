package by.salary.authorizationserver.service;

import by.salary.authorizationserver.authentication.VerificationCodeHandler;
import by.salary.authorizationserver.model.TokenEntity;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.MailRequestDTO;
import by.salary.authorizationserver.model.entity.MailType;
import com.netflix.discovery.converters.Auto;
import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

@Service
@Slf4j
public class TokenRegistrationService {

    MailService mailService;
    JwtService jwtService;
    TokenService tokenService;
    private static BigInteger BILLION;


    @Autowired
    TokenRegistrationService(
            JwtService service,
            TokenService tokenService,
            MailService mailService) {
        this.tokenService = tokenService;
        this.jwtService = service;
        this.mailService = mailService;
        BILLION = BigInteger.valueOf(999_999_999L);
    }

    public String generateToken(UserInfoDTO userInfo, MailType mailType) {
        //generate verification code
        if(!userInfo.is2FEnabled()){
            return is2FDisabled(userInfo);
        }

        if (!userInfo.is2FVerified()){
            return is2FEnabledNotVerified(userInfo, mailType);
        }

        return is2FVerified(userInfo);
    }

    private String is2FDisabled(UserInfoDTO userInfoDTO){
        //generate jwt
        String token = jwtService.generateToken(userInfoDTO);
        TokenEntity tokenEntity = TokenEntity.builder()
                .username(userInfoDTO.getUsername())
                .authenticationToken(token)
                .verificationCode(null)
                .build();

        tokenService.save(tokenEntity);

        return token;
    }

    private String is2FEnabledNotVerified(UserInfoDTO userInfo, MailType mailType){
        userInfo.setAuthorities(Collections.emptyList());
        //generate jwt
        String token = jwtService.generateToken(userInfo);
        String verificationCode = generateVerificationCode();
        log.info("Verification code for email {} is {}", userInfo.getEmail(), verificationCode);

        TokenEntity tokenEntity = TokenEntity.builder()
                .authenticationToken(token)
                .username(userInfo.getUsername())
                .verificationCode(verificationCode)
                .build();

        //TODO: send verification code to email
        MailRequestDTO mailRequestDTO = MailRequestDTO.builder()
                .mailTo(userInfo.getEmail())
                .message(verificationCode)
                .mailType(mailType)
                .build();

        mailService.sendMessage(mailRequestDTO, token);

        //save token in db
        tokenService.save(tokenEntity);


        return token;
    }

    private String is2FVerified(UserInfoDTO userInfoDTO){
        //generate jwt
        String token = jwtService.generateToken(userInfoDTO);
        TokenEntity tokenEntity = TokenEntity.builder()
                .username(userInfoDTO.getUsername())
                .authenticationToken(token)
                .verificationCode(null)
                .build();

        tokenService.save(tokenEntity);

        //send response to db

        return token;
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        BigInteger code;
        do {
            code = new BigInteger(BILLION.bitLength(), random);
        } while (code.compareTo(BILLION) > 0);
        log.info("Generated verification code <{}>", code);
        Formatter formatter = new Formatter();
        return formatter.format("%09d", code).toString();
    }
}
