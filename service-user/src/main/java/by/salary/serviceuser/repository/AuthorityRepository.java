package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.Authority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, BigInteger> {

    Optional<Authority> findByAuthority(String authority);

    boolean existsByAuthority(String user);
}
