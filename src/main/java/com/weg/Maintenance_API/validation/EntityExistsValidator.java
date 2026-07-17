package com.weg.Maintenance_API.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EntityExistsValidator implements ConstraintValidator<EntityExists, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;

    @Override
    public void initialize(EntityExists constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
    }

    @Override
    public boolean isValid(Object id, ConstraintValidatorContext context) {
        if (id == null) {
            return true;
        }

        return entityManager.find(entityClass, id) != null;
    }
}

