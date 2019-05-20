package com.example.datatables.persistence.repository;

import com.example.datatables.persistence.entities.AbstractEntity;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractDataTableRepository<E extends AbstractEntity> extends DataTablesRepository<E, Long> {
}
