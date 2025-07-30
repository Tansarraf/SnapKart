package com.tanishk.project.repo;

import org.springframework.data.domain.Pageable; 

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tanishk.project.model.Category;
import com.tanishk.project.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,JpaSpecificationExecutor<Product>{

//	  JpaSpecificationExecutor -> interface to allow the execution of specifications 
//								  based on the JPA criteria API
	Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);
	Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
	Page<Product> findByCategory(Category category, Pageable pageDetails);

}
