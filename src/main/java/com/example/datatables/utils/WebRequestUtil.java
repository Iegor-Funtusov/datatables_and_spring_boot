package com.example.datatables.utils;

import com.example.datatables.present.container.PageDataContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

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
}
