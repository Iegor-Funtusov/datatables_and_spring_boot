package org.springframework.data.jpa.datatables.easy.service.impl;

import org.springframework.data.jpa.datatables.easy.service.DataTableService;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DataTableServiceImpl<E> implements DataTableService<E> {

    @Override
    public DataTablesOutput<E> findAll(DataTablesInput dataTablesInput, DataTablesRepository<E, Long> dataTablesRepository) {
        return dataTablesRepository.findAll(dataTablesInput);
    }

    @Override
    public DataTablesOutput<E> findAll(DataTablesInput dataTablesInput, Specification<E> specification, DataTablesRepository<E, Long> dataTablesRepository) {
        return dataTablesRepository.findAll(dataTablesInput, specification);
    }
}
