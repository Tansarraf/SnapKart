package com.tanishk.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tanishk.project.model.Category;

public interface CategoryRepo extends JpaRepository<Category,Long>{

	Category findByCategoryName(String categoryName);

}
