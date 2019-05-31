package com.example.datatables.service;

import com.example.datatables.persistence.entities.AbstractEntity;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;

public interface AbstractDataTableService<E extends AbstractEntity> {

    DataTablesOutput<E> findAll(DataTablesInput input);
    DataTablesOutput<E> findAll(DataTablesInput input, Specification<E> specification);
    void generateStartEndTime(Column column);
}
