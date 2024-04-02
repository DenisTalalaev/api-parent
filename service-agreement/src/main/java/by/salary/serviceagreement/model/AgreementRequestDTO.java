package by.salary.serviceagreement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AgreementRequestDTO {
    private BigInteger id;
}
