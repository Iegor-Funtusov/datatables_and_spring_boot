package com.example.datatables.service.impl;

import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.repository.EmployeeDataTableRepository;
import com.example.datatables.persistence.repository.EmployeeRepository;
import com.example.datatables.service.AbstractDataTableService;
import com.example.datatables.service.RepositoryProcessService;

import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeDataTableService implements AbstractDataTableService<Employee> {

    private final EmployeeDataTableRepository employeeDataTableRepository;
    private final EmployeeRepository employeeRepository;
    private final RepositoryProcessService<Employee> repositoryProcessService;

    public EmployeeDataTableService(
            EmployeeDataTableRepository employeeDataTableRepository,
            EmployeeRepository employeeRepository,
            RepositoryProcessService<Employee> repositoryProcessService) {
        this.employeeDataTableRepository = employeeDataTableRepository;
        this.employeeRepository = employeeRepository;
        this.repositoryProcessService = repositoryProcessService;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Employee> findAll(DataTablesInput input) {
        return employeeDataTableRepository.findAll(input);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Employee> findAll(DataTablesInput input, Specification<Employee> specification) {
        return employeeDataTableRepository.findAll(input, specification);
    }

    @Override
    @Transactional(readOnly = true)
    public void generateStartEndTime(Column column) {
        repositoryProcessService.generateStartEndTime(column, employeeRepository);
    }
}
