package by.salary.serviceuser.model;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class PaginationCriteria {

    private Map<PaginationType, String> paginationMap;

    public Map<PaginationType, String> getPaginationMap() {
        return paginationMap;
    }

    public boolean hasPagination() {
        return paginationMap != null && !paginationMap.isEmpty();
    }

    public enum PaginationType {
        LIMIT("limit"),
        OFFSET("offset"),;
        String value;

        PaginationType(String value) {
            this.value = value;
        }

        public static PaginationType valueOfString(String value) {
            for (PaginationType paginationType : PaginationType.values()) {
                if (paginationType.value.equals(value)) {
                    return paginationType;
                }
            }
            throw new IllegalArgumentException("Wrong pagination type");
        }
    }
}
