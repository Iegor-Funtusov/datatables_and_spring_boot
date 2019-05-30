package com.example.datatables.service.impl;

import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.repository.EmployeeDataTableRepository;
import com.example.datatables.persistence.repository.EmployeeRepository;
import com.example.datatables.service.AbstractDataTableService;
import com.example.datatables.utils.DateUtil;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class EmployeeDataTableService implements AbstractDataTableService<Employee> {

    private final EmployeeDataTableRepository employeeDataTableRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeDataTableService(EmployeeDataTableRepository employeeDataTableRepository, EmployeeRepository employeeRepository) {
        this.employeeDataTableRepository = employeeDataTableRepository;
        this.employeeRepository = employeeRepository;
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

    @Transactional(readOnly = true)
    public void generateStartEndTime(DataTablesInput input, String columnName) {
        Column column = input.getColumn(columnName);
        Date start = employeeRepository.findMinCreateTime();
        Date end = employeeRepository.findMaxCreateTime();
        column.setSearch(new Search(DateUtil.generateDateRangeModel(start, end), false));
    }
}
