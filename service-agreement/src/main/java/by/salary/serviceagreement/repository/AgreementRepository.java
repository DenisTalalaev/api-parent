package by.salary.serviceagreement.repository;

import by.salary.serviceagreement.entities.Agreement;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface AgreementRepository extends CrudRepository<Agreement, BigInteger> {

    public Optional<Agreement> findById(BigInteger id);

}
