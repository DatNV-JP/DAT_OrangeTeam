package com.orange.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateOrderStatus {
    private Long orderId;
    private Long orderStatusId;
}
