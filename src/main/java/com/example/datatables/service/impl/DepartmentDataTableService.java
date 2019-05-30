package com.example.datatables.service.impl;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.repository.DepartmentDataTableRepository;
import com.example.datatables.persistence.repository.DepartmentRepository;
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
public class DepartmentDataTableService implements AbstractDataTableService<Department> {

    private final DepartmentDataTableRepository departmentDataTableRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentDataTableService(DepartmentDataTableRepository departmentDataTableRepository, DepartmentRepository departmentRepository) {
        this.departmentDataTableRepository = departmentDataTableRepository;
        this.departmentRepository = departmentRepository;
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

    @Transactional(readOnly = true)
    public void generateStartEndTime(DataTablesInput input, String columnName) {
        Column column = input.getColumn(columnName);
        Date start = departmentRepository.findMinCreateTime();
        Date end = departmentRepository.findMaxCreateTime();
        column.setSearch(new Search(DateUtil.generateDateRangeModel(start, end), false));
    }
}
