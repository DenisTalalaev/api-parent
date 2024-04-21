package by.salary.serviceuser.model.user.organisation;

import by.salary.serviceuser.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class UserJoinOrganisationResponseDTO {
    HttpStatus status;
    String message;

    public UserJoinOrganisationResponseDTO(User user) {
        this.status = HttpStatus.OK;
        this.message = "User joined organisation successfully";
    }

    public UserJoinOrganisationResponseDTO() {
        this.status = HttpStatus.CONFLICT;
        this.message = "Conflict";
    }


}
