package by.salary.serviceuser.entities;

import by.salary.serviceuser.model.premission.PermissionResponseDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;
    @Size(min = 1, max = 50)
    private String name;
    @Column(unique = true)
    @Size(min = 1, max = 2000)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_permission",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    public Permission(PermissionResponseDTO permissionResponseDTO) {

        this.name = permissionResponseDTO.getName();
        this.description = permissionResponseDTO.getDescription();
    }

    public Permission(String s, String description) {
        this.name = s;
        this.description = description;
    }

    public Permission(PermissionsEnum permissionsEnum) {
        this.name = permissionsEnum.name();
        this.description = permissionsEnum.name() + "  permission";
    }

    public static boolean isPermitted(User user, PermissionsEnum permissionsEnum) {
        return isPermitted(user.getPermissions(), permissionsEnum);
    }

    public static boolean isPermitted(List<Permission> permissions, PermissionsEnum permissionsEnum) {
        for (Permission permission : permissions) {
            if (permission.getName().equals(permissionsEnum.name()) || permission.getName().equals(PermissionsEnum.ALL_PERMISSIONS.name())) {
                return true;
            }
        }
        return false;
    }

    public static List<Permission> parse(String s) {
        List<Permission> permissions = new ArrayList<>();
        for (PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
            if (s.contains(permissionsEnum.name())) {
                permissions.add(new Permission(permissionsEnum));
            }
        }
        return permissions;
    }

    @Override
    public boolean equals(Object o) {
        return this.name.equals(((Permission) o).name) ||
                ((Permission) o).name.equals(new Permission(PermissionsEnum.ALL_PERMISSIONS));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
