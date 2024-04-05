package by.salary.authorizationserver.util;

import by.salary.authorizationserver.model.TokenEntity;
import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
public class JwtService {
    //@Value("${token.signing.key}")
    private String jwtSigningKey;

    private TokenRepository tokenRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public JwtService(
            @Value("${token.signing.key}") String jwtSigningKey,
            TokenRepository tokenRepository,
            PasswordEncoder passwordEncoder) {
        this.jwtSigningKey = jwtSigningKey;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Извлечение списка authorities из токена JWT.
     *
     * @param token токен
     * @return список authorities
     */
    public List<GrantedAuthority> extractAuthorities(String token) {
        List<String> authoritiesStrings = extractClaim(token, claims -> claims.get("authorities", List.class));
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (authoritiesStrings != null && !authoritiesStrings.isEmpty()) {
            for (String authority : authoritiesStrings) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return authorities;
    }

    public String generateToken(AuthenticationResponseDto authenticationResponseDto) {
        return generateToken(mapToUserInfoDto(authenticationResponseDto));
    }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserInfoDTO customUserDetails) {
            claims.put("email", customUserDetails.getEmail());
            claims.put("authorities", customUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(toList()));
        }
        return generateToken(claims, userDetails.getUsername());
    }

    public String generateToken(String userName, String email, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("authorities", authorities);
        return generateToken(claims, email);
    }
    public boolean isTokenValid(String token, AuthenticationResponseDto authenticationResponseDto) {
        return isTokenValid(token, mapToUserInfoDto(authenticationResponseDto));
    }
    private UserInfoDTO mapToUserInfoDto(AuthenticationResponseDto authenticationResponseDto) {
        return UserInfoDTO.builder()
                .name(authenticationResponseDto.getUserName())
                .email(authenticationResponseDto.getUserEmail())
                .authorities(authenticationResponseDto.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toList()))
                .build();
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);

        boolean isCryptoValid = (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);

        if (!isCryptoValid){
            return false;
        }

        List<TokenEntity> tokenEntities = tokenRepository.findAllByUsername(userName);

        boolean isSaved = false;
        for (TokenEntity tokenEntity : tokenEntities){
            if (passwordEncoder.matches(token, tokenEntity.getAuthenticationToken())) {
                isSaved = true;
                break;
            }
        }

        return isSaved;
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @return токен
     */
    private String generateToken(Map<String, Object> extraClaims, String subject) {
        String token = Jwts.builder().setClaims(extraClaims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

        TokenEntity tokenEntity = TokenEntity.builder()
                .authenticationToken(passwordEncoder.encode(token))
                .username(subject)
                .build();

        tokenRepository.save(tokenEntity);

        return token;
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
