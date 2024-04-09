package by.salary.serviceinvitation.filters;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class Permission {
    private BigInteger id;
    private String name;
    private String description;
    private List<Object> users;

    public Permission(PermissionsEnum permissionsEnum) {
        this.name = permissionsEnum.name();
        this.description = permissionsEnum.name() + "  permission";
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
