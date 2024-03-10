package com.orange.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XDate {
    public static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    public static SimpleDateFormat sdfDMY = new SimpleDateFormat("dd/MM/yyyy");

    public static String getDateString(Date date) {
        return sdf.format(date);
    }
    public static Date getTodayDate(String date) {
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
