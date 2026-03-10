package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

	// Custom query methods can be defined here if needed
	// For example:
	// List<Company> findByActive(Integer active);

}
