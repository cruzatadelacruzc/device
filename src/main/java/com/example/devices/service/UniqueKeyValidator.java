package com.example.devices.service;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, String> {

    @Autowired
    private EntityManager entityManager;

    private Class<?> entityClass;

    private String columnName;


    @Override
    public void initialize(UniqueKey constraint) {
        this.entityClass = constraint.entityClass();
        this.columnName = constraint.columnName();
    }

    @Override
    public boolean isValid(String target, ConstraintValidatorContext context) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(entityClass);
        query.select(root).where(cb.equal(root.get(columnName), target));
        TypedQuery<Object> typedQuery = entityManager.createQuery(query);
        List<Object> resultSet = typedQuery.getResultList();
        return resultSet.isEmpty();
    }
}
