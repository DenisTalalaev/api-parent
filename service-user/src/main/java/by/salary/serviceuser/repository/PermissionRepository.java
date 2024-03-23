package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.Permission;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface PermissionRepository extends CrudRepository<Permission, BigInteger> {
}
