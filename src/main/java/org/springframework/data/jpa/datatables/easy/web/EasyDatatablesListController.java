package org.springframework.data.jpa.datatables.easy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.easy.data.PageData;
import org.springframework.data.jpa.datatables.easy.data.SessionData;
import org.springframework.data.jpa.datatables.easy.util.DataTablesUtil;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class EasyDatatablesListController<T> {

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    protected SessionData sessionData;

    protected abstract String getListCode();
    protected abstract DataTablesRepository<T, Long> getDataTableRepository();

    protected String list(Model model, WebRequest webRequest) {
        PageData pageData = updatePageData(webRequest);
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
                List<Order> orders = Arrays.asList(new Order[] { new Order(0, ascDesc) });
                Column sortColumn = new Column();
                sortColumn.setData(fieldName);
                sortColumn.setOrderable(true);
                List<Column> columns = Arrays.asList(new Column[] { sortColumn });

                i.setOrder(orders);
                i.setColumns(columns);
            }
        }

        return i;
    }
}
