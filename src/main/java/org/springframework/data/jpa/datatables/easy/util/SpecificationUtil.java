package org.springframework.data.jpa.datatables.easy.util;

import org.springframework.data.jpa.datatables.easy.data.DateData;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class SpecificationUtil {

    public static List<Predicate> generateSpecificationPredicatesByDateRange(
            List<DateData> dateModels,
            Class<?> entityClass,
            List<Predicate> predicates,
            Root<?> root,
            CriteriaBuilder criteriaBuilder) {

        for (Field field : getAllFieldsByEntity(entityClass)) {
            if (Modifier.isPrivate(field.getModifiers())) {
                for (DateData data : dateModels) {
                    if (Objects.equals(field.getName(), data.getFieldName())) {
                        if (field.getType().isAssignableFrom(Date.class)) {
                            predicates.add(criteriaBuilder.between(root.get(field.getName()), data.getStartDate(), data.getEndDate()));
                        }
                    }
                }
            }
        }

        return predicates;
    }

    private static List<Field> getAllFieldsByEntity(Class<?> entityClass) {
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
