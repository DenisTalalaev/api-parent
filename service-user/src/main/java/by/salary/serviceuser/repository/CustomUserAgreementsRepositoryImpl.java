package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.UserAgreement;
import by.salary.serviceuser.model.FilterCriteria;
import by.salary.serviceuser.model.FilterCriteria.FilterCriteriaType;
import by.salary.serviceuser.model.OrderCriteria;
import by.salary.serviceuser.model.PaginationCriteria;
import by.salary.serviceuser.model.SelectionCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class CustomUserAgreementsRepositoryImpl implements CustomUserAgreementsRepository {

    EntityManager entityManager;

    public static List<String> columns;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    CustomUserAgreementsRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        columns = List.of("agreement_id", "count", "current_base_reward", "moderator_comment", "moderator_name", "time", "user_id", "state");
    }

    @Override
    public List<UserAgreement> findWithCriteria(SelectionCriteria selectionCriteria) throws ParseException {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<UserAgreement> criteriaQuery = criteriaBuilder.createQuery(UserAgreement.class);


        Root<UserAgreement> uar = criteriaQuery.from(UserAgreement.class);
        List<Predicate> predicates = new ArrayList<>();
        criteriaQuery.select(uar);
        if (selectionCriteria.hasFilter()) {
            FilterCriteria filter = selectionCriteria.getFilter();
            addFilter(predicates, filter, criteriaBuilder, uar);
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
        OrderCriteria order = selectionCriteria.getOrder();
        if (selectionCriteria.hasOrder()) {
            List<Order> orders = new ArrayList<>();
            addOrder(orders, order, criteriaBuilder, uar);
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<UserAgreement> query = entityManager.createQuery(criteriaQuery);

        if (selectionCriteria.hasPagination()) {
            PaginationCriteria pagination = selectionCriteria.getPagination();
            for (Map.Entry<PaginationCriteria.PaginationType, String> paginationType : pagination.getPaginationMap().entrySet()) {
                switch (paginationType.getKey()) {
                    case LIMIT -> {
                        query.setMaxResults(Integer.parseInt(paginationType.getValue()));
                    }
                    case OFFSET -> {
                        query.setFirstResult(Integer.parseInt(paginationType.getValue()));
                    }
                }
            }
        }


        return query.getResultList();
    }

    private void addFilter(List<Predicate> predicates, FilterCriteria filter, CriteriaBuilder criteriaBuilder, Root<UserAgreement> uar) throws ParseException {
        for (String column : columns) {
            if (!filter.hasColumn(column)) {
                continue;
            }
            Map<FilterCriteriaType, String> columnFilter = filter.getColumn(column);
            for (var filterType : columnFilter.entrySet()) {
                switch (filterType.getKey()) {
                    case EQUALS -> {
                        predicates.add(
                                criteriaBuilder
                                        .equal(uar.get(column), filterType.getValue())
                        );
                    }
                    case AFTER -> {
                        java.util.Date date = formatter.parse(filterType.getValue());
                        java.sql.Date dateSql = new java.sql.Date(date.getTime());
                        predicates.add(
                                criteriaBuilder.greaterThan(uar.<java.sql.Date>get(column), dateSql)
                        );
                    }
                    case BEFORE -> {
                        java.util.Date date = formatter.parse(filterType.getValue());
                        java.sql.Date dateSql = new java.sql.Date(date.getTime());
                        predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(uar.<java.sql.Date>get(column), dateSql)
                        );
                    }
                    case LESS_THAN -> {
                        predicates.add(
                                criteriaBuilder.lessThan(uar.get(column), filterType.getValue())
                        );
                    }
                    case NOT_EQUALS -> {
                        predicates.add(
                                criteriaBuilder.notEqual(uar.get(column), filterType.getValue())
                        );
                    }
                    case GREATER_THAN -> {
                        predicates.add(
                                criteriaBuilder.greaterThan(uar.get(column), filterType.getValue())
                        );
                    }
                }
            }
        }
    }

    private void addOrder(List<Order> orders, OrderCriteria order, CriteriaBuilder criteriaBuilder, Root<UserAgreement> uar) {
        for (String column : order.getColumnsOrder()) {
            if (!columns.contains(column)) {
                continue;
            }
            OrderCriteria.OrderCriteriaType columnOrder = order.getColumn(column);
            switch (columnOrder) {
                case ASC -> {
                    orders.add(
                            criteriaBuilder
                                    .asc(uar.get(column)
                                    )
                    );
                }
                case DESC -> {
                    orders.add(
                            criteriaBuilder.desc(uar.get(column))
                    );
                }
            }

        }
    }
}
