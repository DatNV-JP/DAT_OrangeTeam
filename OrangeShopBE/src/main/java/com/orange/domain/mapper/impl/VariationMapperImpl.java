package com.orange.domain.mapper.impl;

import com.orange.domain.model.Category;
import com.orange.domain.model.Variation;
import com.orange.domain.mapper.ICategoryMapper;
import com.orange.domain.mapper.IVariationMapper;
import com.orange.domain.dto.VariationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VariationMapperImpl implements IVariationMapper {

    private final ModelMapper modelMapper;
    private final ICategoryMapper categoryMapper;

    @Override
    public Variation toEntity(VariationDTO dto) {
        Variation entity = modelMapper.map(dto, Variation.class);
        entity.setCategory(new Category(dto.getCategoryId()));
        return entity;
    }

    @Override
    public VariationDTO toDto(Variation entity) {
        VariationDTO dto = modelMapper.map(entity, VariationDTO.class);
        dto.setCategoryId(entity.getCategory().getId());
        return dto;
    }
}
