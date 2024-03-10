package com.orange.domain.model.ModelProcedure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OgetOrderByUserID {
    private Long totalCompleted;
    private Long totalCancel;
    private Double totalPaid;
}
