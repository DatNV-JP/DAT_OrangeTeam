package com.orange.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnOrderRequest {
    private Long orderId;
    private String description;
}
