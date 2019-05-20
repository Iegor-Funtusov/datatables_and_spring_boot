package com.example.datatables.present.controller;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.repository.DepartmentRepository;
import com.example.datatables.persistence.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employee/list";
    }

    @GetMapping("/list/{id}")
    public String list(Model model, @PathVariable long id) {
        Department department = departmentRepository.getOne(id);
        model.addAttribute("employees", employeeRepository.findAllByDepartment(department));
        return "employee/list";
    }
}
