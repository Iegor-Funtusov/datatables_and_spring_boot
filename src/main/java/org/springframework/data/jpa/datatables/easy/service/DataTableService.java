package org.springframework.data.jpa.datatables.easy.service;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;

public interface DataTableService<E> {

    DataTablesOutput<E> findAll(DataTablesInput dataTablesInput, DataTablesRepository<E, Long> dataTablesRepository);
    DataTablesOutput<E> findAll(DataTablesInput dataTablesInput, Specification<E> specification, DataTablesRepository<E, Long> dataTablesRepository);
}
