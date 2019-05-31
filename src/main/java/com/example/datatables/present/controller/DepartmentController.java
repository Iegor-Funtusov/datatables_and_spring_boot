package com.example.datatables.present.controller;

import com.example.datatables.model.DateModel;
import com.example.datatables.persistence.entities.Department;
import com.example.datatables.present.container.ColumnDefs;
import com.example.datatables.present.container.PageDataContainer;
import com.example.datatables.service.AbstractSpecificationProcess;
import com.example.datatables.service.impl.DepartmentDataTableService;
import com.example.datatables.utils.DataTablesUtil;
import com.example.datatables.utils.DateUtil;
import com.example.datatables.utils.EntityConstUtil;
import com.example.datatables.utils.WebRequestUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        List<Column> columns = new ArrayList<>();
        for (String columnName : EntityConstUtil.TIME_FIELDS) {
            Column column = dataTablesInput.getColumn(columnName);
            if (column != null) {
                columns.add(column);
            }
        }

        if (columns.stream().noneMatch(column -> DateUtil.dateRegExPattern(column.getSearch().getValue()))) {
            for (Column column : columns) {
                column.setSearch(new Search("", false));
            }
            departments = departmentDataTableService.findAll(dataTablesInput);
            for (Column column : columns) {
                departmentDataTableService.generateStartEndTime(column);
            }
        } else {
            List<DateModel> dateModels = new ArrayList<>();
            for (Column column : columns) {
                String dateValue = column.getSearch().getValue();
                DateModel dateModel = DateUtil.generateDateModel(dateValue, column.getData());
                if (dateModel != null) {
                    dateModels.add(dateModel);
                }
                column.setSearch(new Search("", false));
            }
            if (CollectionUtils.isNotEmpty(dateModels)) {
                departments = departmentDataTableService.findAll(dataTablesInput,
                        new AbstractSpecificationProcess<Department>().generateCriteriaPredicate(dateModels, Department.class));
                for (Column column : columns) {
                    DateModel dateModel = dateModels.stream().filter(dm -> Objects.equals(dm.getFieldName(), column.getData())).findFirst().orElse(null);
                    if (dateModel != null) {
                        column.setSearch(new Search(DateUtil.generateDateRangeModel(dateModel.getStartDate(), dateModel.getEndDate()), false));
                    } else {
                        departmentDataTableService.generateStartEndTime(column);
                    }
                }
            } else {
                departments = departmentDataTableService.findAll(dataTablesInput);
                for (Column column : columns) {
                    departmentDataTableService.generateStartEndTime(column);
                }
            }
        }

        DataTablesUtil.pageDataContainerProcessFinish(container, departments);
        container.setColumnDefs(new ColumnDefs(new int[] {0, 4}, false));

        model.addAttribute("pageDataContainer", container);
        model.addAttribute("departments", departments.getData());
        return "department/list";
    }
}
