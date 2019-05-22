package com.example.datatables.present.controller;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.present.container.ColumnDefs;
import com.example.datatables.present.container.PageDataContainer;
import com.example.datatables.service.impl.DepartmentDataTableService;
import com.example.datatables.utils.DataTablesInputUtil;
import com.example.datatables.utils.WebRequestUtil;
import org.springframework.data.jpa.datatables.mapping.*;
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
            dataTablesInput = DataTablesInputUtil.generateDataTablesInput(Arrays.asList("id", "createTime", "updateTime", "name"), container);
        }
        WebRequestUtil.pageDataContainerProcess(container, dataTablesInput);
        DataTablesOutput<Department> departments = departmentDataTableService.findAll(dataTablesInput);
        container.setTotalElements(departments.getRecordsTotal());
        container.setDisplayStart(WebRequestUtil.generateDisplayStart(container));
        container.setDisplayEnd(WebRequestUtil.generateDisplayEnd(container));
        container.setColumnDefs(new ColumnDefs(new int[] {0, 4}, false));

        model.addAttribute("pageDataContainer", container);
        model.addAttribute("departments", departments.getData());
        return "department/list";
    }
}
