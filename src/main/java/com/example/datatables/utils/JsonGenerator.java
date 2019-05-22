package com.example.datatables.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.StringWriter;

@UtilityClass
public class JsonGenerator {

    public String generateJson(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writer().writeValue(writer, o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(writer);
    }
}
