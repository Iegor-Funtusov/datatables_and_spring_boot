package com.example.datatables.present.controller;

import com.example.datatables.model.DateModel;
import com.example.datatables.persistence.entities.Department;
import com.example.datatables.present.container.ColumnDefs;
import com.example.datatables.present.container.PageDataContainer;
import com.example.datatables.service.impl.DepartmentDataTableService;
import com.example.datatables.utils.DataTablesUtil;
import com.example.datatables.utils.DateUtil;
import com.example.datatables.utils.EntityConstUtil;
import com.example.datatables.utils.WebRequestUtil;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentDataTableService departmentDataTableService;

    public DepartmentController(DepartmentDataTableService departmentDataTableService) {
        this.departmentDataTableService = departmentDataTableService;
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

    private String getPage(Model model, PageDataContainer container) {
        DataTablesInput dataTablesInput = container.getDataTablesInput();
        if (dataTablesInput == null) {
            dataTablesInput = DataTablesUtil.generateDataTablesInput(Arrays.asList(EntityConstUtil.ID, EntityConstUtil.CREATE_TIME, EntityConstUtil.UPDATE_TIME, "name"), container);
        }
        DataTablesUtil.pageDataContainerProcess(container, dataTablesInput);
        DataTablesOutput<Department> departments;

        Column column = dataTablesInput.getColumn(EntityConstUtil.CREATE_TIME);
        String datesValue = column.getSearch().getValue();
        if (datesValue.equals("")) {
            departments = departmentDataTableService.findAll(dataTablesInput);
            departmentDataTableService.generateStartEndTime(dataTablesInput, EntityConstUtil.CREATE_TIME);
        } else {
            DateModel dateModel = DateUtil.generateDateModel(datesValue);
            if (dateModel == null) {
                column.setSearch(new Search("", false));
                departments = departmentDataTableService.findAll(dataTablesInput);
                departmentDataTableService.generateStartEndTime(dataTablesInput, EntityConstUtil.CREATE_TIME);
            } else {
                column.setSearch(new Search("", false));
                departments = departmentDataTableService.findAll(dataTablesInput,
                        (Specification<Department>) (root, criteriaQuery, criteriaBuilder) ->
                                criteriaBuilder.between(root.get(EntityConstUtil.CREATE_TIME), dateModel.getStartDate(), dateModel.getEndDate()));
                column.setSearch(new Search(DateUtil.generateDateRangeModel(dateModel.getStartDate(), dateModel.getEndDate()), false));
            }
        }

        DataTablesUtil.pageDataContainerProcessFinish(container, departments);
        container.setColumnDefs(new ColumnDefs(new int[] {0, 4}, false));

        model.addAttribute("pageDataContainer", container);
        model.addAttribute("departments", departments.getData());
        return "department/list";
    }
}
