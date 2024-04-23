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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class CustomUserAgreementsRepositoryImpl implements CustomUserAgreementsRepository {

    EntityManager entityManager;

    public static List<String> columns;
    private static SimpleDateFormat formatter;

    private final String dateFormat;

    CustomUserAgreementsRepositoryImpl(EntityManager entityManager,
    @Value("${spring.mvc.format.time}") String dateFormat) {
        this.entityManager = entityManager;
        this.dateFormat = dateFormat;
        columns = List.of("agreementStateId", "agreementId", "count", "currentBaseReward", "moderatorComment", "moderatorName", "time", "state");
        formatter = new SimpleDateFormat(dateFormat, Locale.US);
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
            try {
                addFilter(predicates, filter, criteriaBuilder, uar);
            }catch (IllegalArgumentException e){
                throw new ParseException(e.getMessage(), 0);
            }
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
            Map<FilterCriteriaType, String[]> columnFilter = filter.getColumn(column);
            for (var filterType : columnFilter.entrySet()) {
                if (filterType.getValue().length == 0) {
                    throw new IllegalArgumentException("Filter value cannot be empty");
                }
                switch (filterType.getKey()) {
                    case EQUALS -> {
                        String value = filterType.getValue()[0];

                        predicates.add(
                                criteriaBuilder
                                        .equal(uar.<String>get(column), value)
                        );
                    }
                    case AFTER -> {
                        java.util.Date date = formatter.parse(filterType.getValue()[0]);
                        java.sql.Date dateSql = new java.sql.Date(date.getTime());
                        predicates.add(
                                criteriaBuilder.greaterThan(uar.<java.sql.Date>get(column), dateSql)
                        );
                    }
                    case BEFORE -> {
                        java.util.Date date = formatter.parse(filterType.getValue()[0]);
                        java.sql.Date dateSql = new java.sql.Date(date.getTime());
                        predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(uar.<java.sql.Date>get(column), dateSql)
                        );
                    }
                    case LESS_THAN -> {
                        predicates.add(
                                criteriaBuilder.lessThan(uar.get(column), filterType.getValue()[0])
                        );
                    }
                    case NOT_EQUALS -> {
                        predicates.add(
                                criteriaBuilder.notEqual(uar.get(column), filterType.getValue()[0])
                        );
                    }
                    case GREATER_THAN -> {
                        predicates.add(
                                criteriaBuilder.greaterThan(uar.get(column), filterType.getValue()[0])
                        );
                    }
                    case DATE_RANGE -> {
                        if (filterType.getValue().length != 2){
                            throw new IllegalArgumentException("Invalid date range");
                        }
                        java.util.Date firstDate;
                        java.util.Date secondDate;
                        try {
                            firstDate = formatter.parse(filterType.getValue()[0]);
                            secondDate = formatter.parse(filterType.getValue()[1]);
                        }catch (ParseException e){
                            throw new IllegalArgumentException("Invalid date format, required: " + dateFormat);
                        }

                        java.sql.Date firstDateSql = new java.sql.Date(firstDate.getTime());
                        java.sql.Date secondDateSql = new java.sql.Date(secondDate.getTime());

                        predicates.add(
                            criteriaBuilder.between(uar.<java.sql.Date>get(column), firstDateSql, secondDateSql)
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
