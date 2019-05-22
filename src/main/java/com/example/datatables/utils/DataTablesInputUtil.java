package com.example.datatables.utils;

import com.example.datatables.present.container.PageDataContainer;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.data.jpa.datatables.mapping.Search;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DataTablesInputUtil {

    public DataTablesInput generateDataTablesInput(List<String> columnsName, PageDataContainer container) {
        List<Order> orders = new ArrayList<>();
        Order order = new Order(1, "desc");
        orders.add(order);
        List<Column> columns = new ArrayList<>();

        for (String columnName : columnsName) {
            Column column = new Column();
            column.setData(columnName);
            column.setName("");
            if (columnName.equals("id")) {
                column.setSearchable(false);
                column.setOrderable(false);
            } else {
                column.setSearchable(true);
                column.setOrderable(true);
            }
            column.setSearch(new Search("", false));
            columns.add(column);
        }

        DataTablesInput dataTablesInput = new DataTablesInput();
        dataTablesInput.setSearch(new Search("", false));
        dataTablesInput.setColumns(columns);
        dataTablesInput.setOrder(orders);
        dataTablesInput.setLength(container.getSize());
        dataTablesInput.setStart(container.getPage() - 1);
        return dataTablesInput;
    }
}
