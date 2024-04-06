package by.salary.authorizationserver.service;

import by.salary.authorizationserver.model.TokenEntity;
import by.salary.authorizationserver.repository.TokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService {
    TokenRepository tokenRepository;

    public List<TokenEntity> findAllByUsername(String username){
        log.info("Get all tokens for user {}", username);
        return tokenRepository.findAllByUsername(username);
    }

    @CacheEvict(value = "token", key = "#username")
    public void deleteAllByUsername(String username){
        log.info("Delete all tokens for user {}", username);
        tokenRepository.deleteAllByUsername(username);
    }

    @CachePut(value = "token", key = "#tokenEntity.username")
    public TokenEntity save(TokenEntity tokenEntity){
        log.info("Save token for user {}", tokenEntity.getUsername());
        return tokenRepository.save(tokenEntity);
    }

}
