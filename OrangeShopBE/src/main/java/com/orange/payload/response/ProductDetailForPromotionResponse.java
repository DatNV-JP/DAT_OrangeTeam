package com.orange.payload.response;

import com.orange.domain.dto.VariationOptionDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ProductDetailForPromotionResponse {

    private Long id;
    private Long productId;
    private Integer quantity;
    private String images;
    private String nameProduct;
    private Double priceInput;
    private Double priceDefault;
    private Double priceSale;
    private Double maxSale;
    private Boolean status;
    private Set<VariationOptionDTO> variationOptions;
}
