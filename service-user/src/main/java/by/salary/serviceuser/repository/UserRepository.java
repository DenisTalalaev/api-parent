package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, BigInteger> {
    
    Optional<User> findByUserEmail(String email);

    Optional<User> findByUserAuthorisationAttributeKey(String authenticationRegistrationKey);

    Optional<User> findByUsername(String username);

    List<User> findAllByOrganisationId(BigInteger organisationId);

    boolean existsByUserEmail(String email);
}
