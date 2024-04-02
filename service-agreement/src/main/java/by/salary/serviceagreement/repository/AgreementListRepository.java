package by.salary.serviceagreement.repository;

import by.salary.serviceagreement.entities.AgreementList;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface AgreementListRepository extends CrudRepository<AgreementList, BigInteger>
{

}
