package by.salary.serviceuser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequestDTO {
    private BigInteger id;
    private String name;
    private String description;
}
