package com.weg.Maintenance_API.validation;


import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EntityExistsIterableValidator implements ConstraintValidator<EntityExists, Iterable<UUID>> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;

    @Override
    public void initialize(EntityExists constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
    }

    @Override
    public boolean isValid(Iterable<UUID> ids, ConstraintValidatorContext context) {
        if (ids == null) {
            return true;
        }

        for (UUID id : ids) {
            if (id == null || entityManager.find(entityClass, id) == null) {
                return false;
            }
        }

        return true;
    }
}

