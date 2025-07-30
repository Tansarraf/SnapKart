package com.tanishk.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.tanishk.project.exceptions.APIException;
import com.tanishk.project.exceptions.ResourceNotFoundException;
import com.tanishk.project.model.Category;
import com.tanishk.project.payload.CategoryDTO;
import com.tanishk.project.payload.CategoryResponse;
import com.tanishk.project.repo.CategoryRepo;


@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@Override
	public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder) {
		
		Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByandOrder);
		Page<Category> categoryPage = categoryRepo.findAll(pageDetails);
		
		List<Category> categories = categoryPage.getContent();
		if(categories.isEmpty()) throw new APIException("No category created till now");
		
		List<CategoryDTO> categoryDTOs = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class))
				.collect(Collectors.toList());
		
		CategoryResponse categoryResponse = new CategoryResponse();
		
		categoryResponse.setContent(categoryDTOs);
		categoryResponse.setPageNumber(categoryPage.getNumber());
		categoryResponse.setPageSize(categoryPage.getSize());
		categoryResponse.setTotalElements(categoryPage.getTotalElements());
		categoryResponse.setTotalPages(categoryPage.getTotalPages());
		categoryResponse.setLastPage(categoryPage.isLast());

		return categoryResponse;
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		Category category = modelMapper.map(categoryDTO, Category.class);
		Category CategoryFromDB = categoryRepo.findByCategoryName(category.getCategoryName());
		if(CategoryFromDB!=null) throw new APIException("Category with the name " + category.getCategoryName() + " already exists!");
		Category savedCategory = categoryRepo.save(category);
		return modelMapper.map(savedCategory, CategoryDTO.class);
	}
	
	@Override
	public CategoryDTO deleteCategory(Long categoryId) {
		List<Category> categories = categoryRepo.findAll();
		Category category = categories.stream().filter(c -> c.getCategoryId()
				.equals(categoryId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

		categoryRepo.delete(category);
		return modelMapper.map(category, CategoryDTO.class);
	}
	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
		Category savedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));
		Category category = modelMapper.map(categoryDTO, Category.class);
		category.setCategoryId(categoryId);
		savedCategory = categoryRepo.save(category);
		return modelMapper.map(savedCategory, CategoryDTO.class);
		
	}
}
