package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.Organisation;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface OrganisationRepository extends CrudRepository<Organisation, BigInteger> {
}
