package by.salary.serviceuser.repository;

import by.salary.serviceuser.entities.UserAgreement;
import by.salary.serviceuser.model.SelectionCriteria;

import java.text.ParseException;
import java.util.List;

public interface CustomUserAgreementsRepository {

    List<UserAgreement> findWithCriteria(SelectionCriteria selectionCriteria) throws ParseException;
}
