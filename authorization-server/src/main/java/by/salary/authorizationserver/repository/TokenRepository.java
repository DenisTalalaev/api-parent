package by.salary.authorizationserver.repository;

import by.salary.authorizationserver.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    List<TokenEntity> findAllByUsername(String username);

    void deleteAllByUsername(String username);

    Optional<TokenEntity> findByAuthenticationToken(String token);
}
