package org.springframework.data.jpa.datatables.easy.util;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    public static String json(Object o) {
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            om.writer().writeValue(sw, o);
        } catch (Exception e) {
            log.error("Failed to JSON serialize " + o, e);
            return e.getMessage();
        }
        return sw.toString();
    }
}
