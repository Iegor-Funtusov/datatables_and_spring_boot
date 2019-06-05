package com.example.datatables.utils;

import com.example.datatables.model.DateModel;
import com.example.datatables.persistence.entities.AbstractEntity;
import lombok.experimental.UtilityClass;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@UtilityClass
public class SpecificationUtil {

    public List<Predicate> generateSpecificationPredicatesByDateRange(
            List<DateModel> dateModels,
            Class<? extends AbstractEntity> entityClass,
            List<Predicate> predicates,
            Root<? extends AbstractEntity> root,
            CriteriaBuilder criteriaBuilder) {

        for (Field field : getAllFieldsByEntity(entityClass)) {
            if (Modifier.isPrivate(field.getModifiers())) {
                for (DateModel dateModel : dateModels) {
                    if (Objects.equals(field.getName(), dateModel.getFieldName())) {
                        if (field.getType().isAssignableFrom(Date.class)) {
                            predicates.add(criteriaBuilder.between(root.get(field.getName()), dateModel.getStartDate(), dateModel.getEndDate()));
                        }
                    }
                }
            }
        }

        return predicates;
    }

    private List<Field> getAllFieldsByEntity(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> nextClass = entityClass;
        do {
            Field[] innerFields = nextClass.getDeclaredFields();
            fields.addAll(Arrays.asList(innerFields));
            nextClass = nextClass.getSuperclass();
        } while (Objects.nonNull(nextClass));
        return fields;
    }
}
