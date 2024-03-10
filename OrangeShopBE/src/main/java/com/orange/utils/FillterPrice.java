package com.orange.utils;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FillterPrice {
    private String fieldName;
    private Double from;
    private Double to;
}
