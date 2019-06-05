package com.example.datatables.utils;

import com.example.datatables.present.container.PageDataContainer;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.datatables.mapping.*;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DataTablesUtil {

    private final String ORDER_DESC = "desc";
    private final int ZERO_VALUE = 0;
    private final int ONE_VALUE = 1;

    public DataTablesInput generateDataTablesInput(List<String> columnsName, PageDataContainer container) {
        List<Order> orders = new ArrayList<>();
        Order order = new Order(1, ORDER_DESC);
        orders.add(order);
        List<Column> columns = new ArrayList<>();

        for (String columnName : columnsName) {
            Column column = new Column();
            column.setData(columnName);
            column.setName("");
            if (columnName.equals(EntityConstUtil.ID)) {
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
        dataTablesInput.setStart(container.getPage() - ONE_VALUE);

        return dataTablesInput;
    }

    public void pageDataContainerProcess(PageDataContainer container, DataTablesInput dataTablesInput) {
        if (dataTablesInput.getOrder().get(ZERO_VALUE).getColumn() != container.getOrderCol() ||
                ObjectUtils.notEqual(dataTablesInput.getOrder().get(ZERO_VALUE).getDir(), container.getOrderDir())) {
            container.setPage(ONE_VALUE);
            dataTablesInput.setStart(container.getPage() - ONE_VALUE);
        } else {
            dataTablesInput.setStart((container.getPage() - ONE_VALUE) * container.getSize());
        }

        if (dataTablesInput.getLength() != container.getSize()) {
            container.setPage(ONE_VALUE);
            dataTablesInput.setLength(container.getSize());
            dataTablesInput.setStart(container.getPage() - ONE_VALUE);
        }

        List<Order> orders = new ArrayList<>();
        Order order = new Order(container.getOrderCol(), container.getOrderDir());
        orders.add(order);
        dataTablesInput.setOrder(orders);

        container.setDataTablesInput(dataTablesInput);
    }

    public void pageDataContainerProcessFinish(PageDataContainer container, long recordsFiltered) {
        if (recordsFiltered == ZERO_VALUE) {
            container.setTotalElements(ZERO_VALUE);
            container.setDisplayStart(ONE_VALUE);
            container.setDisplayEnd(ZERO_VALUE);
        } else {
            container.setTotalElements(recordsFiltered);
            container.setDisplayStart(generateDisplayStart(container));
            container.setDisplayEnd(generateDisplayEnd(container));
        }
    }

    private long generateDisplayStart(PageDataContainer container) {
        if (container.getTotalElements() == ZERO_VALUE) {
            return ZERO_VALUE;
        } else if (container.getSize() > container.getTotalElements()) {
            return ONE_VALUE;
        } else {
            return container.getSize() * (container.getPage() - ONE_VALUE) + ONE_VALUE;
        }
    }

    private long generateDisplayEnd(PageDataContainer container) {
        int lastSize = container.getSize() * (container.getPage() - ONE_VALUE) + container.getSize();
        if (lastSize <= container.getTotalElements()) {
            return lastSize;
        } else {
            return container.getTotalElements();
        }
    }
}
