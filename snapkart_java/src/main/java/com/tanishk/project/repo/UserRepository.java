package com.tanishk.project.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanishk.project.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String username);
	
	Boolean existsByUserName(String username);
	
	Boolean existsByEmail(String email);
	
	Optional<User> findByEmail(String email);

}
