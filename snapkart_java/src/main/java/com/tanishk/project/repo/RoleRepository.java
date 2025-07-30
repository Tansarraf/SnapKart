package com.tanishk.project.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tanishk.project.model.AppRole;
import com.tanishk.project.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Optional<Role> findByRoleName(AppRole appRole);

}
