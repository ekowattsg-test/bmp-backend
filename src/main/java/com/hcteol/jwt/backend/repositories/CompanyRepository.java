package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

	List<Company> findByActive(Boolean active);

	List<Company> findByShowCompany(Boolean showCompany);

	List<Company> findByActiveAndShowCompany(Boolean active, Boolean showCompany);

	List<Company> findByCity(String city);

	List<Company> findByLanguage(String language);

}
