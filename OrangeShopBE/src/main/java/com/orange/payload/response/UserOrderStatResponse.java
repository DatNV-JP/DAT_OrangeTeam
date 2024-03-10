package com.orange.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserOrderStatResponse {
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private Integer totalOrders;
    private String email;
}
