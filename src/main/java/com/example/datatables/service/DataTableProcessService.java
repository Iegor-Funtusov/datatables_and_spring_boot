package com.example.datatables.service;

import com.example.datatables.model.DateModel;
import com.example.datatables.persistence.entities.AbstractEntity;
import com.example.datatables.utils.DateUtil;
import com.example.datatables.utils.EntityConstUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DataTableProcessService<E extends AbstractEntity> {

    public DataTablesOutput<E> generateDataTablesOutput(
            AbstractDataTableService<E> dataTableService,
            DataTablesInput dataTablesInput,
            Class<E> entityClass) {

        DataTablesOutput<E> output;

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
            output = dataTableService.findAll(dataTablesInput);
            for (Column column : columns) {
                dataTableService.generateStartEndTime(column);
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
                output = dataTableService.findAll(dataTablesInput,
                        new AbstractSpecificationProcess<E>().generateCriteriaPredicate(dateModels, entityClass));
                for (Column column : columns) {
                    DateModel dateModel = dateModels.stream().filter(dm -> Objects.equals(dm.getFieldName(), column.getData())).findFirst().orElse(null);
                    if (dateModel != null) {
                        column.setSearch(new Search(DateUtil.generateDateRangeModel(dateModel.getStartDate(), dateModel.getEndDate()), false));
                    } else {
                        dataTableService.generateStartEndTime(column);
                    }
                }
            } else {
                output = dataTableService.findAll(dataTablesInput);
                for (Column column : columns) {
                    dataTableService.generateStartEndTime(column);
                }
            }
        }

        return output;
    }
}
