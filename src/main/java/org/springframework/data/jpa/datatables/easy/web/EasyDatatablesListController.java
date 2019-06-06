package org.springframework.data.jpa.datatables.easy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.easy.data.PageData;
import org.springframework.data.jpa.datatables.easy.data.SessionData;
import org.springframework.data.jpa.datatables.easy.util.DataTablesUtil;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class EasyDatatablesListController<T> {

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    protected SessionData sessionData;

    protected abstract String getListCode();
    protected abstract DataTablesRepository<T, Long> getDataTableRepository();

    protected String list(Model model, WebRequest webRequest) {
        PageData pageData = updatePageData(webRequest);
        System.out.println("pageData = " + pageData);
        DataTablesOutput<T> dto = getDataTableRepository().findAll(toDataTablesInput(pageData));
        DataTablesUtil.updatePageData(pageData, dto.getRecordsFiltered());
        model.addAttribute(getListCode() + "List", dto.getData());
        model.addAttribute(getListCode() + "Page", pageData);

        return "/" + getListCode() + "/list";
    }

    private PageData updatePageData(WebRequest webRequest) {
        String pageDataParam = webRequest.getParameter("pageData");
        if (StringUtils.isNotBlank(pageDataParam)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(pageDataParam, PageData.class);
            } catch (IOException e) {
                log.error("Failed to JSON serialize " + pageDataParam, e);
                return PageData.getDefault();
            }
        } else {
            PageData pd = getPageData();
            String pageParam = webRequest.getParameter("page");
            if (StringUtils.isNotBlank(pageParam)) {
                int page = Integer.parseInt(pageParam);
                pd.setPage(page);
            }
            String sizeParam = webRequest.getParameter("size");
            if (StringUtils.isNotBlank(sizeParam)) {
                int size = Integer.parseInt(sizeParam);
                pd.setSize(size);
            }
            return pd;
        }
    }

    private PageData getPageData() {
        return sessionData.getOrCreatePageData(this.getClass(), DEFAULT_PAGE_SIZE);
    }

    private DataTablesInput toDataTablesInput(PageData pd) {
        DataTablesInput i = new DataTablesInput();

        i.setLength(pd.getSize());
        i.setStart((DataTablesUtil.generateDisplayStart(pd)) - 1);

        if (StringUtils.isNotBlank(pd.getOrder())) {
            String[] orderSplit = pd.getOrder().split("_");
            if (orderSplit.length == 2 && "asc,desc".contains(orderSplit[1])) {
                String fieldName = orderSplit[0];
                String ascDesc = orderSplit[1];
                List<Order> orders = Collections.singletonList(new Order(0, ascDesc));
                List<Column> columns = Collections.singletonList(initColumns(fieldName, ""));
                i.setOrder(orders);
                i.setColumns(columns);
            }
        }

        if (pd.getFilterMap() != null) {
            List<Column> columns = new ArrayList<>();
            for (Map.Entry<String, String> entry : pd.getFilterMap().entrySet()) {
                columns.add(initColumns(entry.getKey(), entry.getValue()));
            }
            i.setColumns(columns);
        }

        return i;
    }

    private Column initColumns(String columnName, String searchValue) {
        Column column = new Column();
        column.setSearchable(true);
        column.setOrderable(true);
        column.setData(columnName);
        column.setName("");
        column.setSearch(new Search(searchValue, false));
        return column;
    }
}
