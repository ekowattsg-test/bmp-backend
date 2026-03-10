package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.repositories.CompanyRepository;

@Service
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepository;

	public Company addCompany(Company company) {
		return companyRepository.save(company);
	};

	public List<Company> getCompany() {
		return companyRepository.findAll();
	};

	public Company getCompanyById(String companyId) {
		return companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
	};

	public Company updateCompany(String companyId, Company companyDetails) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
		company.setCompanyName(companyDetails.getCompanyName());
		company.setShowCompany(companyDetails.getShowCompany());
		company.setActive(companyDetails.getActive());
		return companyRepository.save(company);
	};

	public void deleteCompany(String companyId) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
		companyRepository.delete(company);
	};
}
