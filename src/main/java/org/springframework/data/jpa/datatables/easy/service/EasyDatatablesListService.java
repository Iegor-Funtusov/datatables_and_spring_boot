package org.springframework.data.jpa.datatables.easy.service;

import org.springframework.data.jpa.datatables.easy.data.DataTablesData;
import org.springframework.data.jpa.datatables.easy.data.PageData;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface EasyDatatablesListService<T> {

    DataTablesOutput<T> getDataTablesOutput(DataTablesData<T> data, DataTablesRepository<T, Long> repository);
    DataTablesOutput<T> getDataTablesOutput(PageData pageData);
}
