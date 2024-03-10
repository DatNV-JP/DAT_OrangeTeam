package com.orange.services.impl;

import com.orange.common.payload.Page;
import com.orange.domain.dto.CategoryDTO;
import com.orange.domain.mapper.ICategoryMapper;
import com.orange.domain.model.Category;
import com.orange.domain.model.Product;
import com.orange.exception.GlobalException;
import com.orange.payload.response.CategoryResponse;
import com.orange.repositories.ICategoryRepository;
import com.orange.repositories.IProductRepository;
import com.orange.services.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryRepository categoryRepository;
    private final ModelMapper mapper;
    private final ICategoryMapper categoryMapper;
    private final IProductRepository productRepository;

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        dto.setStatus(true);
        Category category;
        if (dto.getParentCategoryId() != null){
            Category categoryParent = this.categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> GlobalException.throwException("category.error.duplicate.id"));
            category = categoryMapper.toEntity(dto);
            category.setParentCategory(categoryParent);
        }
        else {
            category = categoryMapper.toEntity(dto);
        }
        category = categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDTO update(CategoryDTO dto) {
        if (dto.getId() == dto.getParentCategoryId()){
            throw GlobalException.throwException("category.error.duplicate.id");
        }
        List<Category> categories = categoryRepository.getAllRelationsById(dto.getId());
        for (int i = 0; i < categories.size(); i++) {if (dto.getParentCategoryId() == categories.get(i).getId()) {throw GlobalException.throwException("category.error.parent");}}
        Category category = categoryMapper.toEntity(dto);

        if (dto.getParentCategoryId() !=  null){
            Category cateParent = categoryRepository.findById(dto.getParentCategoryId()).orElseThrow();
            category.setParentCategory(cateParent);
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO delete(CategoryDTO dto) {
        Product product = productRepository.find1ProductByCategory(dto.getId(),true);
        Category categoryChild = categoryRepository.findChildById(dto.getId());
        if (product != null || categoryChild != null){
            throw GlobalException.throwException("category.error.delete");
        }
        dto.setStatus(false);
        Category category = categoryRepository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(category);
    }

    @Override
    public Page<?> fillAll(Pageable pageable) {
        org.springframework.data.domain.Page<Category> result = this.categoryRepository.findAllByStatusIsTrue(pageable);
        List<CategoryResponse> categoryResponses = result.getContent()
                .stream()
                .map(c -> {
                    CategoryResponse cr = this.mapper.map(c, CategoryResponse.class);
                    if (c.getParentCategory() != null) {
                    }
                    return cr;
                })
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), categoryResponses);
    }

    @Override
    public List<CategoryResponse> findByStatusTrue() {
        List<Category> result = this.categoryRepository.findAllByStatusIsTrueAndParentCategoryIsNull();
        return result.stream()
                .map(c -> {
                    CategoryResponse cr = this.mapper.map(c, CategoryResponse.class);
                    if (c.getParentCategory() != null) {
                        cr.setParentId(c.getParentCategory().getId());
                    }
                    return cr;
                }).collect(Collectors.toList());
    }


    /**
     * Map category to category ressponse
     */
    CategoryResponse getCategoryChildrend(Category category) {
        CategoryResponse cr = this.mapper.map(category, CategoryResponse.class);
        if (category.getParentCategory() != null) {
            cr.setParentId(category.getParentCategory().getId());
        }
        if(!category.getChildren().isEmpty()) {
            Set<CategoryResponse> setCR = new HashSet<>();
            for ( Category c : category.getChildren() ) {
                setCR.add(getCategoryChildrend(c));
            }
//            cr.setChildren(setCR);
        }
        return cr;
    };

    @Override
    public CategoryDTO findById(Long aLong) {
        Category category = this.categoryRepository.findById(aLong)
                .orElseThrow(() -> GlobalException.throwException("address.user.id.notfound"));
        return this.mapper.map(category, CategoryDTO.class);
    }
}
