package by.salary.serviceuseragreement.repository;

import by.salary.serviceuseragreement.entities.UserAgreement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserAgreementRepository extends CrudRepository<UserAgreement, BigInteger> {
}
