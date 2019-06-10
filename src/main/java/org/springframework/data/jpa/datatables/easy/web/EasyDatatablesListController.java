package org.springframework.data.jpa.datatables.easy.web;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.easy.data.DataTablesData;
import org.springframework.data.jpa.datatables.easy.data.PageData;
import org.springframework.data.jpa.datatables.easy.data.SessionData;
import org.springframework.data.jpa.datatables.easy.service.EasyDatatablesListService;
import org.springframework.data.jpa.datatables.easy.util.DataTablesUtil;
import org.springframework.data.jpa.datatables.easy.util.SpecificationUtil;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

public abstract class EasyDatatablesListController<T> {

	private static final String FILTER_PARAM_PREFIX = "filter_";
	private static final int DEFAULT_PAGE_SIZE = 10;

	@Autowired
	protected SessionData sessionData;

	protected abstract String getListCode();

	protected abstract DataTablesRepository<T, Long> getDataTableRepository();
	protected abstract EasyDatatablesListService<T> getEasyDatatablesListService();

	protected String list(Model model, WebRequest webRequest) {
		PageData pageData = updatePageData(webRequest);

		DataTablesOutput<T> dto;
		if (getDataTableRepository() != null) {
			DataTablesData<T> data = toDataTablesInput(pageData);
			dto = getEasyDatatablesListService().getDataTablesOutput(data, getDataTableRepository());
		} else {
			// this implementation does not use jpa
			dto = getEasyDatatablesListService().getDataTablesOutput(pageData);
		}

		DataTablesUtil.updatePageData(pageData, dto.getRecordsFiltered());
		model.addAttribute(getListCode() + "List", dto.getData());
		model.addAttribute(getListCode() + "Page", pageData);

		return "/" + getListCode() + "/list";
	}

	private PageData updatePageData(WebRequest webRequest) {
		PageData pd = getPageData();
		if (webRequest.getParameter("clear") != null) {
			pd.clear();
		}

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

		String orderParam = webRequest.getParameter("order");
		if (StringUtils.isNotBlank(sizeParam)) {
			pd.setOrder(orderParam);
		}

		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		for (String paramName : parameterMap.keySet()) {
			if (paramName.startsWith(FILTER_PARAM_PREFIX)) {
				pd.addFilterValue(paramName.substring(FILTER_PARAM_PREFIX.length()), parameterMap.get(paramName));
			}
		}
		return pd;
	}

	private PageData getPageData() {
		return sessionData.getOrCreatePageData(this.getClass(), DEFAULT_PAGE_SIZE);
	}

	private DataTablesData<T> toDataTablesInput(PageData pd) {
		DataTablesInput i = new DataTablesInput();
		Map<String, String> specificValueMap = new HashMap<>();

		i.setLength(pd.getSize());
		i.setStart(((pd.getPage() - 1) * pd.getSize()));

		List<Column> columns = new ArrayList<>();

		if (StringUtils.isNotBlank(pd.getOrder())) {
			String[] orderSplit = pd.getOrder().split("_");
			if (orderSplit.length == 2 && "asc,desc".contains(orderSplit[1])) {
				String fieldName = orderSplit[0];
				String ascDesc = orderSplit[1];
				List<Order> orders = Collections.singletonList(new Order(0, ascDesc));
				columns.add(initColumns(fieldName, ""));
				i.setOrder(orders);
				i.setColumns(columns);
			}
		}

		if (pd.getFilterMap() != null) {
			for (Map.Entry<String, String> entry : pd.getFilterMap().entrySet()) {
				columns.add(initColumns(entry.getKey(), entry.getValue()));
			}
			if (!columns.isEmpty()) {
				for (Column column : columns) {
					if (column.getData().contains(".") && StringUtils.isNotBlank(column.getSearch().getValue())) {
						Search search = column.getSearch();
						String field = column.getData();
						String fieldName = field.substring(0, field.indexOf("."));
						specificValueMap.put(fieldName, search.getValue());
						search.setValue("");
					}
				}
				i.setColumns(columns);
			}
		}

		DataTablesData<T> data = new DataTablesData<>();
		data.setInput(i);
		if (!specificValueMap.isEmpty()) {
			ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
			@SuppressWarnings("unchecked")
			Class<T> entityClass = (Class<T>) superclass.getActualTypeArguments()[0];
			Specification<T> spec = new SpecificationUtil<T>().generateFinishSpecification(specificValueMap, entityClass);
			data.setSpecification(spec);
		}

		return data;
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
