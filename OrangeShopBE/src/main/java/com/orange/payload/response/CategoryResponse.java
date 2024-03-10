package com.orange.payload.response;

import com.orange.domain.model.Category;
import lombok.Data;

import java.util.Set;

@Data
public class CategoryResponse {
    private Long id;
    private Long parentId;
    private String name;
    private Set<Category> children;
}
