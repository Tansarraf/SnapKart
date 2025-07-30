package com.tanishk.project.service;

import com.tanishk.project.payload.CategoryDTO;
import com.tanishk.project.payload.CategoryResponse;

public interface CategoryService {
	
	CategoryResponse getAllCategories(Integer pageNumer,Integer pageSize, String sortBy, String sortOrder);
	CategoryDTO createCategory(CategoryDTO categoryDTO);
	CategoryDTO deleteCategory(Long categoryId);
	CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
