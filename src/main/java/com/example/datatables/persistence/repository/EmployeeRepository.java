package com.example.datatables.persistence.repository;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.enums.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByDepartment(Department department);

    List<Employee> findAllByPosition(Position position);
    List<Employee> findAllByPositionAndDepartment(Position position, Department department);
}
