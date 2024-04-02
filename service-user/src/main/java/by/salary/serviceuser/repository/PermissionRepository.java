package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.Permission;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends CrudRepository<Permission, BigInteger> {
    boolean existsByName(String name);

    Optional<Permission> findByName(String s);
}
