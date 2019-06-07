package org.springframework.data.jpa.datatables.easy.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class DataTablesData<E> {

    private DataTablesInput input;
    private Specification<E> specification;
}
