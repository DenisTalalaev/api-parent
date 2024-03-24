package by.salary.serviceagreement.repository;

import by.salary.serviceagreement.entities.AgreementStatesList;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface AgreementStateListRepository  extends CrudRepository<AgreementStatesList, BigInteger>
{

}
