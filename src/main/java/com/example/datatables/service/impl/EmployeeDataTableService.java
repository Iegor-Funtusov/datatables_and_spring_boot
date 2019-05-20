package com.example.datatables.service.impl;

import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.repository.EmployeeDataTableRepository;
import com.example.datatables.service.AbstractDataTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeDataTableService implements AbstractDataTableService<Employee> {

    private final EmployeeDataTableRepository employeeDataTableRepository;

    public EmployeeDataTableService(EmployeeDataTableRepository employeeDataTableRepository) {
        this.employeeDataTableRepository = employeeDataTableRepository;
    }

    @Override
    public DataTablesOutput<Employee> findAll(DataTablesInput input) {
        return employeeDataTableRepository.findAll(input);
    }
}
