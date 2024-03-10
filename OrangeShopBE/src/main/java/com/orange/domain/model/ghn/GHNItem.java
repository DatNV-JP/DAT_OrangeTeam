package com.orange.domain.model.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class GHNItem {

    @NotNull
    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @NotNull
    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("price")
    private int price;

    @JsonProperty("length")
    private int length;

    @JsonProperty("width")
    private int width;

    @JsonProperty("height")
    private int height;

    @JsonProperty("category")
    private GHNCategory category;
}
