package by.salary.serviceinvitation.repository;

import by.salary.serviceinvitation.entities.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, BigInteger>{
}
