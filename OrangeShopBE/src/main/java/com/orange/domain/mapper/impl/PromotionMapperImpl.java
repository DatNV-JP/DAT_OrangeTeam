package com.orange.domain.mapper.impl;

import com.orange.domain.dto.PromotionDTO;
import com.orange.domain.mapper.IProductDetailMapper;
import com.orange.domain.mapper.IPromotionMapper;
import com.orange.domain.model.ProductDetail;
import com.orange.domain.model.Promotion;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PromotionMapperImpl implements IPromotionMapper {
    private final ModelMapper modelMapper;
    private final IProductDetailMapper productDetailMapper;
    @Override
    public Promotion toEntity(PromotionDTO dto) {
        Promotion entity = modelMapper.map(dto, Promotion.class);
        if (dto.getProductDetailDTOs() == null) {
            Set<ProductDetail> productDetails= new HashSet<>();
            entity.setProductDetails(productDetails);
        } else if (!dto.getProductDetailDTOs().isEmpty()){
            entity.setProductDetails(productDetailMapper.toEntitySet(dto.getProductDetailDTOs()));
        }

        return entity;
    }

    @Override
    public PromotionDTO toDto(Promotion entity) {
        PromotionDTO dto = modelMapper.map(entity, PromotionDTO.class);
        dto.setProductDetailDTOs(productDetailMapper.toDtoSet(entity.getProductDetails()));
        return dto;
    }
}
