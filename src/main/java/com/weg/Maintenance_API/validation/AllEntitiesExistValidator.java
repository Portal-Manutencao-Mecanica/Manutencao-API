package com.weg.Maintenance_API.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class AllEntitiesExistValidator implements ConstraintValidator<AllEntitiesExist, Iterable<Long>> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;

    @Override
    public void initialize(AllEntitiesExist annotation) {
        this.entityClass = annotation.entityClass();
    }

    @Override
    public boolean isValid(Iterable<Long> ids, ConstraintValidatorContext context) {
        if (ids == null) {
            return true;
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null) {
                return false;
            }
            uniqueIds.add(id);
        }

        if (uniqueIds.isEmpty()) {
            return true;
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<?> root = query.from(entityClass);

        EntityType<?> entityType = entityManager.getMetamodel().entity(entityClass);
        @SuppressWarnings("rawtypes")
        Class idType = entityType.getIdType().getJavaType();
        String idAttribute = entityType.getId(idType).getName();

        query.select(criteriaBuilder.count(root))
                .where(root.get(idAttribute).in(uniqueIds));

        Long existingCount = entityManager.createQuery(query).getSingleResult();
        return Objects.equals(existingCount, (long) uniqueIds.size());
    }
}
