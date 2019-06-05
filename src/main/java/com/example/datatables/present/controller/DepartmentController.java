package com.example.datatables.present.controller;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.present.container.ColumnDefs;
import com.example.datatables.present.container.PageDataContainer;
import com.example.datatables.service.DataTableProcessService;
import com.example.datatables.service.impl.DepartmentDataTableService;
import com.example.datatables.utils.DataTablesUtil;
import com.example.datatables.utils.EntityConstUtil;
import com.example.datatables.utils.WebRequestUtil;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
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
    private final DataTableProcessService<Department> dataTableProcessService;

    public DepartmentController(DepartmentDataTableService departmentDataTableService, DataTableProcessService<Department> dataTableProcessService) {
        this.departmentDataTableService = departmentDataTableService;
        this.dataTableProcessService = dataTableProcessService;
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
        DataTablesOutput<Department> departments = dataTableProcessService.generateDataTablesOutput(departmentDataTableService, dataTablesInput, Department.class);

        DataTablesUtil.pageDataContainerProcessFinish(container, departments.getRecordsFiltered());
        container.setColumnDefs(new ColumnDefs(new int[]{0, 4}, false));

        model.addAttribute("pageDataContainer", container);
        model.addAttribute("departments", departments.getData());
        return "department/list";
    }
}
