package by.salary.serviceuser.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    private String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    public Authority(AuthorityEnum authority) {
        this.authority = authority.name();
    }
}
