package com.example.datatables.service.impl;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.repository.DepartmentDataTableRepository;
import com.example.datatables.service.AbstractDataTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DepartmentDataTableService implements AbstractDataTableService<Department> {

    private final DepartmentDataTableRepository departmentDataTableRepository;

    public DepartmentDataTableService(DepartmentDataTableRepository departmentDataTableRepository) {
        this.departmentDataTableRepository = departmentDataTableRepository;
    }

    @Override
    public DataTablesOutput<Department> findAll(DataTablesInput input) {
        return departmentDataTableRepository.findAll(input);
    }
}
