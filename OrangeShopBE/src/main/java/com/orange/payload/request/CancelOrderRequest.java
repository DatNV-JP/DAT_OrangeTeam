package com.orange.payload.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
public class CancelOrderRequest {
    @NotNull
    private Long orderId;
    @NotNull
    private String reason;
}
