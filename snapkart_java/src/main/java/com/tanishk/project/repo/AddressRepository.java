package com.tanishk.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tanishk.project.model.Address;

public interface AddressRepository extends JpaRepository<Address , Long>{
	
}
