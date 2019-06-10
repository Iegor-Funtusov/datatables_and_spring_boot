package com.example.datatables.service;

import org.springframework.data.jpa.datatables.easy.data.DataTablesData;
import org.springframework.data.jpa.datatables.easy.data.PageData;
import org.springframework.data.jpa.datatables.easy.service.EasyDatatablesListService;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Service;

@Service
public class EasyDatatablesListServiceImpl<T> implements EasyDatatablesListService<T> {
    
    @Override
    public DataTablesOutput<T> getDataTablesOutput(DataTablesData<T> data, DataTablesRepository<T, Long> repository) {
        DataTablesOutput<T> dto;
        try {
            if (data.getSpecification() == null) {
                dto = repository.findAll(data.getInput());
            } else {
                dto = repository.findAll(data.getInput(), data.getSpecification());
            }
        } catch (Exception e) {
            dto = new DataTablesOutput<>();
            dto.setError(e.getMessage());
        }
        return dto;
    }

    @Override
    public DataTablesOutput<T> getDataTablesOutput(PageData pageData) {
        return null;
    }
}
