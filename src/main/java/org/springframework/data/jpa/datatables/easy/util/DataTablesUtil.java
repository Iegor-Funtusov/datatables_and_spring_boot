package org.springframework.data.jpa.datatables.easy.util;

import org.springframework.data.jpa.datatables.easy.data.PageData;

public class DataTablesUtil {

    private static final int ZERO_VALUE = 0;
    private static final int ONE_VALUE = 1;

    public static void updatePageData(PageData pageData, long recordsFiltered) {
        if (recordsFiltered == ZERO_VALUE) {
            pageData.setTotalElements(ZERO_VALUE);
            pageData.setDisplayStart(ONE_VALUE);
            pageData.setDisplayEnd(ZERO_VALUE);
        } else {
            pageData.setTotalElements(recordsFiltered);
            pageData.setDisplayStart(generateDisplayStart(pageData));
            pageData.setDisplayEnd(generateDisplayEnd(pageData));
        }
    }

    private static int generateDisplayStart(PageData pageData) {
        if (pageData.getTotalElements() == ZERO_VALUE) {
            return ZERO_VALUE;
        } else if (pageData.getSize() > pageData.getTotalElements()) {
            return ONE_VALUE;
        } else {
            return pageData.getSize() * (pageData.getPage() - ONE_VALUE) + ONE_VALUE;
        }
    }

    private static long generateDisplayEnd(PageData pageData) {
        int lastSize = pageData.getSize() * (pageData.getPage() - ONE_VALUE) + pageData.getSize();
        if (lastSize <= pageData.getTotalElements()) {
            return lastSize;
        } else {
            return pageData.getTotalElements();
        }
    }
}
