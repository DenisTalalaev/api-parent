package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface UserRepository extends CrudRepository<User, BigInteger> {
}
