package by.salary.authorizationserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeVerifiedEmailRequestDto {

    private String email;
    private Boolean is2FEnabled;
}
