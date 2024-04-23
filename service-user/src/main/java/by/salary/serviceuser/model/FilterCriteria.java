package by.salary.serviceuser.model;

import lombok.*;

import java.util.Map;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterCriteria {


    // map of column name, filter criteria and value
    //Example:
    /*
    filter: {
        name: {
            equals: "John",
            equals: "Jane"
        }
        date: {
            after: "2020-01-01"
        }
     }
     */
    Map<String, Map<FilterCriteriaType, String[]>> filterMap;

    public boolean hasColumn(String columnName) {
        return filterMap.containsKey(columnName);
    }
    public Map<FilterCriteriaType, String[]> getColumn(String columnName) {
        return filterMap.get(columnName);
    }

    public boolean hasFilter(){
        return filterMap != null && !filterMap.isEmpty();
    }

    public enum FilterCriteriaType {
        EQUALS("equals"),
        NOT_EQUALS("notEquals"),
        GREATER_THAN("greaterThan"),
        LESS_THAN("lessThan"),
        AFTER("after"),
        BEFORE("before"),
        DATE_RANGE("dateRange");
        final String value;
        FilterCriteriaType(String value) {
            this.value = value;
        }

        public static FilterCriteriaType valueOfString(String value) {
            for (FilterCriteriaType type : FilterCriteriaType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
