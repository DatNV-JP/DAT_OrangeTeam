package com.orange.utils;

import com.orange.exception.EntityInvalid;

public class FilterUtils {
    public static FillterPrice getFilterPrice(String filter) {
        try {
            String[] field_value = filter.split(",");
            String fieldName = field_value[0];
            String[] values = field_value[1].split(":");
            Double from = Double.parseDouble(values[0]);
            Double to = Double.parseDouble(values[1]);
            return FillterPrice.builder()
                    .fieldName(fieldName)
                    .from(from)
                    .to(to)
                    .build();
        } catch (Exception e) {
            throw new EntityInvalid("Invalid Params Filter");
        }
    }
}
