package com.orange.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class VariationDTOProduct {
    @NotNull
    private Long categoryId;
    @Size(max = 105)
    @NotNull
    private String name;
    private Boolean status;
}
