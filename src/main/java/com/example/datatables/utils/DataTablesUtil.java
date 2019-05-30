package com.example.datatables.utils;

import com.example.datatables.persistence.entities.AbstractEntity;
import com.example.datatables.present.container.PageDataContainer;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.datatables.mapping.*;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DataTablesUtil {

    public DataTablesInput generateDataTablesInput(List<String> columnsName, PageDataContainer container) {
        List<Order> orders = new ArrayList<>();
        Order order = new Order(1, "desc");
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
        dataTablesInput.setStart(container.getPage() - 1);
        return dataTablesInput;
    }

    public void pageDataContainerProcess(PageDataContainer container, DataTablesInput dataTablesInput) {
        if (dataTablesInput.getOrder().get(0).getColumn() != container.getOrderCol() ||
                ObjectUtils.notEqual(dataTablesInput.getOrder().get(0).getDir(), container.getOrderDir())) {
            container.setPage(1);
            dataTablesInput.setStart(container.getPage() - 1);
        } else {
            dataTablesInput.setStart((container.getPage() - 1) * container.getSize());
        }

        if (dataTablesInput.getLength() != container.getSize()) {
            container.setPage(1);
            dataTablesInput.setLength(container.getSize());
            dataTablesInput.setStart(container.getPage() - 1);
        }

        List<Order> orders = new ArrayList<>();
        Order order = new Order(container.getOrderCol(), container.getOrderDir());
        orders.add(order);
        dataTablesInput.setOrder(orders);

        container.setDataTablesInput(dataTablesInput);
    }

    public void pageDataContainerProcessFinish(PageDataContainer container, DataTablesOutput<? extends AbstractEntity> output) {
        if (output.getRecordsFiltered() == 0) {
            container.setTotalElements(0);
            container.setDisplayStart(1);
            container.setDisplayEnd(0);
        } else {
            container.setTotalElements(output.getRecordsFiltered());
            container.setDisplayStart(generateDisplayStart(container));
            container.setDisplayEnd(generateDisplayEnd(container));
        }
    }

    private long generateDisplayStart(PageDataContainer container) {
        if (container.getTotalElements() == 0) {
            return 0;
        } else if (container.getSize() > container.getTotalElements()) {
            return 1;
        } else {
            return container.getSize() * (container.getPage() - 1) + 1;
        }
    }

    private long generateDisplayEnd(PageDataContainer container) {
        int lastSize = container.getSize() * (container.getPage() - 1) + container.getSize();
        if (lastSize <= container.getTotalElements()) {
            return lastSize;
        } else {
            return container.getTotalElements();
        }
    }
}
