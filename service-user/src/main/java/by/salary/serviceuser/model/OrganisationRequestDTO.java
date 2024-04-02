package by.salary.serviceuser.model;

import by.salary.serviceuser.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrganisationRequestDTO {


    private BigInteger id;
    private String organisationName;
    private String organisationAddress;

    private String organisationContactNumber;
    private BigDecimal baseReward;

    private String directorFirstName;
    private String directorSurname;
    private String directorSecondName;
}
