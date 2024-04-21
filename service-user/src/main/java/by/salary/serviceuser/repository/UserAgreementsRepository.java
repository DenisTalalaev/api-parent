package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.UserAgreement;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface UserAgreementsRepository extends CrudRepository<UserAgreement, BigInteger>, CustomUserAgreementsRepository{
}
