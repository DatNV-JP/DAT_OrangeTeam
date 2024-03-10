package com.orange.services;

import com.orange.domain.dto.CategoryDTO;
import com.orange.payload.response.CategoryResponse;

import java.util.List;

public interface ICategoryService extends BaseService<CategoryDTO, Long>{
    List<CategoryResponse> findByStatusTrue();
}
