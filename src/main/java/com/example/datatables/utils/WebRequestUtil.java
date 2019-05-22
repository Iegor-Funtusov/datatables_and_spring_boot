package com.example.datatables.utils;

import com.example.datatables.present.container.PageDataContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class WebRequestUtil {

    public PageDataContainer getPageDataContainerByWebRequest(WebRequest webRequest) {
        String datatableParam = webRequest.getParameter("datatable");
        if (StringUtils.isBlank(datatableParam)) {
            return null;
        }
        PageDataContainer pageDataContainer;
        ObjectMapper mapper = new ObjectMapper();
        try {
            pageDataContainer = mapper.readValue(datatableParam, PageDataContainer.class);
            return pageDataContainer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    public long generateDisplayStart(PageDataContainer container) {
        if (container.getTotalElements() == 0) {
            return 0;
        } else if (container.getSize() > container.getTotalElements()) {
            return 1;
        } else {
            return container.getSize() * (container.getPage() - 1) + 1;
        }
    }

    public long generateDisplayEnd(PageDataContainer container) {
        int lastSize = container.getSize() * (container.getPage() - 1) + container.getSize();
        if (lastSize <= container.getTotalElements()) {
            return lastSize;
        } else {
            return container.getTotalElements();
        }
    }
}
