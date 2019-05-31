package com.example.datatables.service;

import com.example.datatables.model.DateModel;
import com.example.datatables.persistence.entities.AbstractEntity;
import com.example.datatables.utils.SpecificationUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AbstractSpecificationProcess<E extends AbstractEntity> {

    public Specification<E> generateCriteriaPredicate(List<DateModel> dateModels, Class<E> entityClass) {
        return (Specification<E>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = SpecificationUtil.generateSpecificationPredicatesByDateRange(dateModels, entityClass, new ArrayList<>(), root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
