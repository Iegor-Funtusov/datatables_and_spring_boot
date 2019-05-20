package com.example.datatables.present.controller;

import com.example.datatables.persistence.repository.DepartmentRepository;
import com.example.datatables.service.impl.DepartmentDataTableService;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final DepartmentDataTableService departmentDataTableService;

    public DepartmentController(DepartmentRepository departmentRepository, DepartmentDataTableService departmentDataTableService) {
        this.departmentRepository = departmentRepository;
        this.departmentDataTableService = departmentDataTableService;
    }

    @GetMapping("/list")
    public String list(Model model) {
//        model.addAttribute("departments", departmentRepository.findAll(PageRequest.of(0, 10)));
        model.addAttribute("departments", departmentRepository.findAll());
        return "department/list";
    }

    @PostMapping("/list/data")
    public String listData(Model model, WebRequest webRequest) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "department/list";
    }
}

//input = DataTablesInput(
// draw=1,
// start=0,
// length=10,
// search=Search(value=, regex=false),
// order=[Order(column=1, dir=desc)],
// columns=[
// Column(data=id, name=, searchable=false, orderable=false, search=Search(value=, regex=false)),
// Column(data=createTime, name=, searchable=true, orderable=true, search=Search(value=, regex=false)),
// Column(data=organisation, name=, searchable=true, orderable=true, search=Search(value=, regex=false)),
// Column(data=receiverIdentifier, name=, searchable=true, orderable=true, search=Search(value=, regex=false)),
// Column(data=documentStatus, name=, searchable=true, orderable=true, search=Search(value=, regex=false)),
// Column(data=documentType, name=, searchable=true, orderable=true, search=Search(value=, regex=false)),
// Column(data=ingoingDocumentFormat, name=, searchable=true, orderable=true, search=Search(value=, regex=false)),
// Column(data=senderName, name=, searchable=true, orderable=true, search=Search(value=, regex=false))])


//out = DataTablesOutput(
// draw=1,
// recordsTotal=16,
// recordsFiltered=16,
// data=[AbstractCreateUpdateEntity(updateTime=2019-05-10 09:36:22.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:36:09.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:35:58.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:35:43.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:35:26.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:35:12.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:34:50.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:34:39.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:34:27.0), AbstractCreateUpdateEntity(updateTime=2019-05-10 09:34:08.0)],
// error=null)
