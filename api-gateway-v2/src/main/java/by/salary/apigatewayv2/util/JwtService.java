package by.salary.apigatewayv2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;


@Service
public class JwtService {
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_IS_2F_ENABLED = "is2FEnabled";
    private static final String CLAIM_IS_2F_VERIFIED = "is2FVerified";

    public boolean is2FEnabled(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_IS_2F_ENABLED, Boolean.class);
    }

    public boolean is2FVerified(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_IS_2F_VERIFIED, Boolean.class);
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
        return claims.get(CLAIM_EMAIL, String.class);
    }

    public boolean hasAuthority(String token, String authority){
        //if route doesn't need authority return true
        if (authority == null){
            return true;
        }
        List<String> authorities = extractAuthorities(token);
        for(String thisAuthority : authorities){
            if (thisAuthority.equals(authority))
                return true;
        }
        return false;
    }



    public List<String> extractAuthorities(String token) {
        return (List<String>) extractClaim(token, claims -> claims.get(CLAIM_AUTHORITIES, List.class));
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
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        int i = token.lastIndexOf('.');
        String withoutSignature = token.substring(0, i+1);

        return (Claims) Jwts.parser().parse(withoutSignature).getBody();
    }
}
