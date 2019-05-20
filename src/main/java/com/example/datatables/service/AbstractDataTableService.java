package com.example.datatables.service;

import com.example.datatables.persistence.entities.AbstractEntity;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

public interface AbstractDataTableService<E extends AbstractEntity> {

    DataTablesOutput<E> findAll(DataTablesInput input);
}
