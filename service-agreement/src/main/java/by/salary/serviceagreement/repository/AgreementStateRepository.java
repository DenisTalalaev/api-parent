package by.salary.serviceagreement.repository;

import by.salary.serviceagreement.entities.AgreementState;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface AgreementStateRepository extends CrudRepository<AgreementState, BigInteger> {
}
