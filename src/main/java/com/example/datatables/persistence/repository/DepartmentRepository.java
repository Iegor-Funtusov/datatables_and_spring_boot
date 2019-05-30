package com.example.datatables.persistence.repository;

import com.example.datatables.persistence.entities.Department;

import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends AbstractJpaRepository<Department> {
}
