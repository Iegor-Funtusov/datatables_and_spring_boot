package com.example.datatables.present;

import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.enums.Position;
import com.example.datatables.persistence.repository.EmployeeDataTableRepository;

import com.example.datatables.service.EasyDatatablesListServiceImpl;
import org.springframework.data.jpa.datatables.easy.service.EasyDatatablesListService;
import org.springframework.data.jpa.datatables.easy.web.EasyDatatablesListController;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@Controller
@RequestMapping("/employee")
public class EmployeeController extends EasyDatatablesListController<Employee> {

    private final EmployeeDataTableRepository employeeDataTableRepository;
    private final EasyDatatablesListServiceImpl<Employee> employeeEasyDatatablesListService;

    public EmployeeController(EmployeeDataTableRepository employeeDataTableRepository, EasyDatatablesListServiceImpl<Employee> employeeEasyDatatablesListService) {
        this.employeeDataTableRepository = employeeDataTableRepository;
        this.employeeEasyDatatablesListService = employeeEasyDatatablesListService;
    }

    @GetMapping("/list")
    public String list(Model model, WebRequest webRequest) {
        preInitModel(model);
        return super.list(model, webRequest);
    }

    @PostMapping("/list")
    public String listData(Model model, WebRequest webRequest) {
        preInitModel(model);
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

    @Override
    protected EasyDatatablesListService<Employee> getEasyDatatablesListService() {
        return this.employeeEasyDatatablesListService;
    }

    private void preInitModel(Model model) {
        model.addAttribute("positionEnums", Arrays.asList(Position.values()));
    }
}
