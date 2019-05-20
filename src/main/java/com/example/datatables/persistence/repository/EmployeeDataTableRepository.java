package com.example.datatables.persistence.repository;

import com.example.datatables.persistence.entities.Employee;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDataTableRepository extends AbstractDataTableRepository<Employee> {
}
