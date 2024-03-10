package com.orange.services;

import com.orange.domain.dto.VariationDTO;

import java.util.List;

public interface IVariationService extends BaseService<VariationDTO,Long>{

    List<?> getAllByCategory(Long categoryId);
}
