package com.example.datatables.present.controller;

import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.present.container.ColumnDefs;
import com.example.datatables.present.container.PageDataContainer;
import com.example.datatables.service.impl.EmployeeDataTableService;
import com.example.datatables.utils.DataTablesUtil;
import com.example.datatables.utils.WebRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeDataTableService employeeDataTableService;

    public EmployeeController(EmployeeDataTableService employeeDataTableService) {
        this.employeeDataTableService = employeeDataTableService;
    }

    @GetMapping("/list")
    public String list(Model model) {
        return getPage(model, new PageDataContainer());
    }

    @PostMapping("/list")
    public String listData(Model model, WebRequest webRequest) {
        PageDataContainer container = WebRequestUtil.getPageDataContainerByWebRequest(webRequest);
        if (container != null) {
            return getPage(model, container);
        } else {
            return getPage(model, new PageDataContainer());
        }
    }

    @GetMapping("/list/{id}")
    public String list(Model model, @PathVariable long id, WebRequest webRequest) {
        PageDataContainer container = WebRequestUtil.getPageDataContainerByWebRequest(webRequest);
        if (container != null) {
            Column column = container.getDataTablesInput().getColumn("department.id");
            column.setSearchValue(String.valueOf(id));
            return getPage(model, container);
        } else {
            container = new PageDataContainer();
            DataTablesInput dataTablesInput = generateDataTablesInputByEmployee(container);
            Column column = dataTablesInput.getColumn("department.id");
            column.setSearchValue(String.valueOf(id));
            container.setDataTablesInput(dataTablesInput);
            return getPage(model, container);
        }
    }

    private String getPage(Model model, PageDataContainer container) {
        DataTablesInput dataTablesInput = container.getDataTablesInput();
        if (dataTablesInput == null) {
            dataTablesInput = generateDataTablesInputByEmployee(container);
        }
        DataTablesUtil.pageDataContainerProcess(container, dataTablesInput);
        DataTablesOutput<Employee> employees = employeeDataTableService.findAll(dataTablesInput);
        DataTablesUtil.pageDataContainerProcessFinish(container, employees);
        container.setColumnDefs(new ColumnDefs(new int[] { 0 }, false));

        model.addAttribute("pageDataContainer", container);
        model.addAttribute("employees", employees.getData());
        return "employee/list";
    }

    private DataTablesInput generateDataTablesInputByEmployee(PageDataContainer container) {
        return DataTablesUtil.generateDataTablesInput(Arrays.asList("id", "createTime", "updateTime", "position", "firstName", "lastName", "salary", "department.id"), container);
    }

    private Specification<Employee> generateSpecification(String id) {
        return (Specification<Employee>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("department").get("id"), Long.parseLong(id)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
