package by.salary.serviceuser.model.changeemail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEmailRequestDto {

    String email;
    Boolean is2FEnabled;

    public Boolean is2FEnabled() {
        if (is2FEnabled == null) {
            return false;
        }
        return is2FEnabled;
    }
}
