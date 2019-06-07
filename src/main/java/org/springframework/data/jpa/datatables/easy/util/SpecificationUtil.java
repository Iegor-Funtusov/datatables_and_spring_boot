package org.springframework.data.jpa.datatables.easy.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.datatables.easy.data.DateData;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Slf4j
public class SpecificationUtil<T> {

    private static final String REGEX_VALID_NUM = "^[0-9]+$";

    public Specification<T> generateFinishSpecification(Map<String, String> specificValueMap, Class<T> entityClass) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(generateSpecificationPredicates(specificValueMap, entityClass, root, criteriaBuilder));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<Predicate> generateSpecificationPredicates(
            Map<String, String> specificValueMap,
            Class<?> entityClass,
            Root<?> root,
            CriteriaBuilder criteriaBuilder) {

        String containsLikePattern;
        List<Predicate> predicates = new ArrayList<>();

        for (Field field : getAllFieldsByEntity(entityClass)) {
            if (Modifier.isPrivate(field.getModifiers())) {
                String fieldValue = specificValueMap.get(field.getName());
                if (fieldValue != null) {
                    List<Predicate> innerEntitiesPredicates = new ArrayList<>();
                    for (Field innerField : getAllFieldsByEntity(field.getType())) {
                        if (Modifier.isPrivate(innerField.getModifiers())) {
                            if (!field.getType().isPrimitive()) {
                                if (String.class.isAssignableFrom(innerField.getType())) {
                                    containsLikePattern = getContainsLikePattern(fieldValue);
                                    innerEntitiesPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName()).get(innerField.getName())), containsLikePattern));
                                }
                                if (Long.class.isAssignableFrom(innerField.getType())) {
                                    if (fieldValue.matches(REGEX_VALID_NUM)) {
                                        try {
                                            Long fieldNumber = Long.parseLong(fieldValue);
                                        innerEntitiesPredicates.add(criteriaBuilder.equal(root.get(field.getName()).get(innerField.getName()), fieldNumber));
                                        } catch (NumberFormatException e) {
                                            log.error("Failed parse long from string " + fieldValue, e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(innerEntitiesPredicates)) {
                        predicates.add(criteriaBuilder.or(innerEntitiesPredicates.toArray(new Predicate[0])));
                    }
                }
            }
        }

        return predicates;
    }

    private static List<Predicate> generateSpecificationPredicatesByDateRange(
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

    private static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
