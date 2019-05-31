package com.example.datatables.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class EntityConstUtil {

    public String ID = "id";
    public String CREATE_TIME = "createTime";
    public String UPDATE_TIME = "updateTime";

    public List<String> TIME_FIELDS = Arrays.asList(CREATE_TIME, UPDATE_TIME);
}
