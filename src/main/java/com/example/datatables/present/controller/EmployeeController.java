package com.example.datatables.present.controller;

import com.example.datatables.model.DateModel;
import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.enums.Position;
import com.example.datatables.present.container.ColumnDefs;
import com.example.datatables.present.container.PageDataContainer;
import com.example.datatables.service.impl.EmployeeDataTableService;
import com.example.datatables.utils.DataTablesUtil;
import com.example.datatables.utils.DateUtil;
import com.example.datatables.utils.EntityConstUtil;
import com.example.datatables.utils.WebRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

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
        DataTablesOutput<Employee> employees;

        Column column = dataTablesInput.getColumn(EntityConstUtil.CREATE_TIME);
        String datesValue = column.getSearch().getValue();
        if (datesValue.equals("")) {
            employees = employeeDataTableService.findAll(dataTablesInput);
            employeeDataTableService.generateStartEndTime(dataTablesInput, EntityConstUtil.CREATE_TIME);
        } else {
            DateModel dateModel = DateUtil.generateDateModel(datesValue, EntityConstUtil.CREATE_TIME);
            if (dateModel == null) {
                column.setSearch(new Search("", false));
                employees = employeeDataTableService.findAll(dataTablesInput);
                employeeDataTableService.generateStartEndTime(dataTablesInput, EntityConstUtil.CREATE_TIME);
            } else {
                column.setSearch(new Search("", false));
                employees = employeeDataTableService.findAll(dataTablesInput,
                        (Specification<Employee>) (root, criteriaQuery, criteriaBuilder) ->
                                criteriaBuilder.between(root.get(EntityConstUtil.CREATE_TIME), dateModel.getStartDate(), dateModel.getEndDate()));
                column.setSearch(new Search(DateUtil.generateDateRangeModel(dateModel.getStartDate(), dateModel.getEndDate()), false));
            }
        }

        DataTablesUtil.pageDataContainerProcessFinish(container, employees);
        container.setColumnDefs(new ColumnDefs(new int[] { 0 }, false));

        model.addAttribute("pageDataContainer", container);
        model.addAttribute("employees", employees.getData());
        model.addAttribute("positions", Position.values());
        return "employee/list";
    }

    private DataTablesInput generateDataTablesInputByEmployee(PageDataContainer container) {
        return DataTablesUtil.generateDataTablesInput(Arrays.asList(EntityConstUtil.ID, EntityConstUtil.CREATE_TIME, "position", "firstName", "lastName", "salary", "department.id"), container);
    }
}
