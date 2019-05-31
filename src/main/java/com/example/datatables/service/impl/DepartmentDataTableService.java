package com.example.datatables.service.impl;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.repository.DepartmentDataTableRepository;
import com.example.datatables.persistence.repository.DepartmentRepository;
import com.example.datatables.service.AbstractDataTableService;
import com.example.datatables.service.RepositoryProcessService;

import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentDataTableService implements AbstractDataTableService<Department> {

    private final DepartmentDataTableRepository departmentDataTableRepository;
    private final DepartmentRepository departmentRepository;
    private final RepositoryProcessService<Department> repositoryProcessService;

    public DepartmentDataTableService(
            DepartmentDataTableRepository departmentDataTableRepository,
            DepartmentRepository departmentRepository,
            RepositoryProcessService<Department> repositoryProcessService) {
        this.departmentDataTableRepository = departmentDataTableRepository;
        this.departmentRepository = departmentRepository;
        this.repositoryProcessService = repositoryProcessService;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Department> findAll(DataTablesInput input) {
        return departmentDataTableRepository.findAll(input);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Department> findAll(DataTablesInput input, Specification<Department> specification) {
        return departmentDataTableRepository.findAll(input, specification);
    }

    @Override
    @Transactional(readOnly = true)
    public void generateStartEndTime(Column column) {
        repositoryProcessService.generateStartEndTime(column, departmentRepository);
    }
}
