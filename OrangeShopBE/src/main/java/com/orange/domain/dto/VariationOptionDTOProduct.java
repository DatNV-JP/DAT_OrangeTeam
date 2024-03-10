package com.orange.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Data
public class    VariationOptionDTOProduct {
    @NotNull
    private VariationDTO variation;
    @Size(max = 505)
    private String value;
}
