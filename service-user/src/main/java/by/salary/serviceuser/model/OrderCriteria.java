package by.salary.serviceuser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class OrderCriteria {

    Map<String, OrderCriteriaType> orderMap;


    public OrderCriteriaType getColumn(String columnName) {

        return orderMap.get(columnName);
    }

    public List<String> getColumnsOrder() {
        return orderMap.keySet().stream().toList();
    }

    public boolean hasOrder() {
        return orderMap != null && !orderMap.isEmpty();
    }
    public boolean hasColumn(String columnName) {
        return orderMap.containsKey(columnName);
    }
    public enum OrderCriteriaType {
        ASC("asc"),
        DESC("desc");

        private String value;
        OrderCriteriaType(String value) {
            this.value = value;
        }

        public static OrderCriteriaType valueOfString(String value) {
            for (OrderCriteriaType type : OrderCriteriaType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
