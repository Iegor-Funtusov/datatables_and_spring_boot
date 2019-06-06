package org.springframework.data.jpa.datatables.easy.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
//import java.util.Map;

@Getter
@Setter
@ToString
public class PageData implements Serializable {

    private static final long serialVersionUID = -2954553322587783857L;
    private static final PageData DEFAULT = new PageData();

    private int page = 1;
    private int size = 10;
    private long totalElements = 0;
    private long displayStart = 1;
    private long displayEnd = size;

    /*
     * "title asc", "id desc"
     */
    private String order;

    /*
     * "title" -> "My name", "createDate" -> "20.02.2019 - 30.05.2019"
     */
//    private Map<String, String> filterMap;

    public static PageData getDefault() {
        return DEFAULT;
    }
}
