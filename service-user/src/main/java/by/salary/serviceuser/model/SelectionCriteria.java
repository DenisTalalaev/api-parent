package by.salary.serviceuser.model;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class SelectionCriteria {

    Map<String, Map<FilterCriteria.FilterCriteriaType, String[]>> filter;

    Map<String, OrderCriteria.OrderCriteriaType>  order;

    Map<PaginationCriteria.PaginationType, String> pagination;

    public FilterCriteria getFilter() {
        return FilterCriteria.builder()
                .filterMap(filter)
                .build();
    }

    public OrderCriteria getOrder() {
        return OrderCriteria.builder()
                .orderMap(order)
                .build();
    }

    public PaginationCriteria getPagination() {
        return PaginationCriteria.builder()
                .paginationMap(pagination)
                .build();
    }

    public boolean hasFilter() {
        return filter != null && !filter.isEmpty();
    }

    public boolean hasOrder() {
        return order != null && !order.isEmpty();
    }

    public boolean hasPagination() {
        return pagination != null && !pagination.isEmpty();
    }
}
