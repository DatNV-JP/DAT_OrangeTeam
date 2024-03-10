package com.orange.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orange.domain.model.Category;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
public class PromotionDTO extends BaseEntityDTO implements Serializable {
    @NotBlank
    private String name;
    @NotBlank
    private String description;

    @NotNull
//    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss aa",timezone = "Asia/Ho_Chi_Minh")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH", timezone =  "Asia/Ho_Chi_Minh")
    private Date startDate;

    @NotNull
//    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss aa",timezone = "Asia/Ho_Chi_Minh")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH", timezone =  "Asia/Ho_Chi_Minh")
    private Date endDate;

    @NotNull
    private Integer discount;

    @NotNull
    private String discountCode;
    @NotNull
    private Boolean status;
    @NotNull
    private Boolean isPercent;
    @NotNull
    private Boolean isDate;
    private Set<ProductDetailDTO> productDetailDTOs;

}
