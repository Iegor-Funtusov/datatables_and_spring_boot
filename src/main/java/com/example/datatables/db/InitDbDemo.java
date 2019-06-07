package com.example.datatables.db;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.enums.Position;
import com.example.datatables.persistence.repository.DepartmentRepository;
import com.example.datatables.persistence.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InitDbDemo {

    private final NameDict nameDict;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public InitDbDemo(NameDict nameDict, DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.nameDict = nameDict;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public void init() {

        nameDict.getDepartmentNameSet().forEach(dep -> {

            Department department = new Department();
            department.setName(dep);
            department = departmentRepository.save(department);

            for (int qq = 0; qq < 38; qq++) {
                Employee employee = new Employee();
                employee.setPosition(Position.WORKER);
                List<Employee> employees = employeeRepository.findAllByPosition(Position.OWNER);
                if (employees.size() == 0) {
                    employee.setPosition(Position.OWNER);
                }
                employees = employeeRepository.findAllByPositionAndDepartment(Position.DIRECTOR, department);
                if (employees.size() == 0) {
                    employee.setPosition(Position.DIRECTOR);
                }
                employees = employeeRepository.findAllByPositionAndDepartment(Position.MANAGER, department);
                if (employees.size() <= 10) {
                    employee.setPosition(Position.MANAGER);
                }
                employees = employeeRepository.findAllByPositionAndDepartment(Position.ACCOUNTANT, department);
                if (employees.size() <= 5) {
                    employee.setPosition(Position.ACCOUNTANT);
                }
                employee.setDepartment(department);
                employee.setFirstName(nameDict.getRandomFirstName());
                employee.setLastName(nameDict.getRandomLastName());
                employee.setSalary(random());
                employeeRepository.save(employee);
            }
        });
    }

    private BigDecimal random() {
        int range = 10000;
        BigDecimal max = new BigDecimal(range);
        BigDecimal randFromDouble = new BigDecimal(Math.random());
        BigDecimal actualRandomDec = randFromDouble.multiply(max);
        actualRandomDec = actualRandomDec.setScale(2, BigDecimal.ROUND_DOWN);
        return actualRandomDec;
    }
}
