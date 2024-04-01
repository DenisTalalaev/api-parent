package by.salary.authorizationserver.model.entity;


import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    public Authority(String authority) {
        this.authority = authority;
    }

    private BigInteger id;

    private String authority;
}
