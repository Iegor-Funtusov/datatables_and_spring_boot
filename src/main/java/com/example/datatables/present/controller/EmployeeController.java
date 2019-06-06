package com.example.datatables.present.controller;

import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.repository.EmployeeDataTableRepository;

import org.springframework.data.jpa.datatables.easy.web.EasyDatatablesListController;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/employee")
public class EmployeeController extends EasyDatatablesListController<Employee> {

    private final EmployeeDataTableRepository employeeDataTableRepository;

    public EmployeeController(EmployeeDataTableRepository employeeDataTableRepository) {
        this.employeeDataTableRepository = employeeDataTableRepository;
    }

    @GetMapping("/list")
    public String list(Model model, WebRequest webRequest) {
        return super.list(model, webRequest);
    }

    @PostMapping("/list")
    public String listData(Model model, WebRequest webRequest) {
        return super.list(model, webRequest);
    }

    @Override
    protected String getListCode() {
        return "employee";
    }

    @Override
    protected DataTablesRepository<Employee, Long> getDataTableRepository() {
        return this.employeeDataTableRepository;
    }
}
