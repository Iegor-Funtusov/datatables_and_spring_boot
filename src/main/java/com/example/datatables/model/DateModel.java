package com.example.datatables.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DateModel {

    private String fieldName;
    private Date startDate;
    private Date endDate;
}
