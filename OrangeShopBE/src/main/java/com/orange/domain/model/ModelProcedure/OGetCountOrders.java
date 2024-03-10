package com.orange.domain.model.ModelProcedure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OGetCountOrders {
    private String orderDateThisWeek;
    private Integer orderCountThisWeek;
    private Double totalSoldThisWeek;
    private Integer countComplatedThisWeek;
    private Integer countCancelThisWeek;
    //last week
    private String orderDateLastWeek;
    private Integer orderCountLastWeek;
    private Double totalSoldLastWeek;
    private Integer countComplatedLastWeek;
    private Integer countCancelLastWeek;
}
