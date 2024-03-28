package by.salary.serviceinvitation.repository;

import by.salary.serviceinvitation.entities.Invitation;
import com.fasterxml.jackson.annotation.OptBoolean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, BigInteger>{
    Optional<Invitation> findByInvitationCode(String userInvitationCode);

    boolean existsByInvitationCode(String userInvitationCode);
}
