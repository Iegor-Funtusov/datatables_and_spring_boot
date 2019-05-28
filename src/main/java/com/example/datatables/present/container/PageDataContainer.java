package com.example.datatables.present.container;

import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;

import java.io.Serializable;

@Data
public class PageDataContainer implements Serializable {

    private static final long serialVersionUID = 84982870159183974L;

    private int page = 1;
    private int size = 10;
    private long totalElements = 0;
    private long displayStart = 1;
    private long displayEnd = 10;

    private int orderCol = 1;
    private String orderDir = "desc";

    private ColumnDefs columnDefs = new ColumnDefs();
    private DataTablesInput dataTablesInput = null;
}
