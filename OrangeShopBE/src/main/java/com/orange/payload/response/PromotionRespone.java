package com.orange.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Builder
public class PromotionRespone {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH", timezone =  "Asia/Ho_Chi_Minh")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH", timezone =  "Asia/Ho_Chi_Minh")
    private Date endDate;
    private Integer discount;
    private String discountCode;
    private Boolean status;
    private Boolean isPercent;
    private Boolean isDate;
    private Set<ProductDetailForPromotionResponse> productDetailDTOs;
}
