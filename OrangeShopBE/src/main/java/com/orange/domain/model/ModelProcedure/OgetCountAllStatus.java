package com.orange.domain.model.ModelProcedure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OgetCountAllStatus {
    private Integer completed;
    private Integer cancel;
    private Integer waitForPay;
    private Integer deliveryInProgress;
    private Integer waitForConfirmation;
    private Integer confirmed;
}
