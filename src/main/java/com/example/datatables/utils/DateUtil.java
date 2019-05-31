package com.example.datatables.utils;

import com.example.datatables.model.DateModel;
import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class DateUtil {

    private static final String REGEX_CREATE_TIME_RANGE = "^[0-9]{13}:[0-9]{13}$";

    public String generateDateRangeModel(Date start, Date end) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(start) + " - " + format.format(end);
    }

    public DateModel generateDateModel(String dates, String fieldName) {
        if (dateRegExPattern(dates)) {
            String[] datesArray = dates.split(":");
            Date start = new Date(Long.parseLong(datesArray[0]));
            Date end = new Date(Long.parseLong(datesArray[1]));
            return new DateModel(fieldName, start, end);
        } else {
            return null;
        }
    }

    public boolean dateRegExPattern(String parameter) {
        return parameter.matches(REGEX_CREATE_TIME_RANGE);
    }
}
