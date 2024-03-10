package com.orange.payload.request;

import lombok.Data;

@Data
public class CheckVoucherRequest {
    private String username;
    private Long voucherId;
    private Double orderValue;
}
