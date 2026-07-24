package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.dtos.CompanyDto;
import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.repositories.CompanyRepository;

@Service
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepository;

	public CompanyDto addCompany(CompanyDto companyDto) {
		Company company = dtoToEntity(companyDto);
		Company savedCompany = companyRepository.save(company);
		return entityToDto(savedCompany);
	};

	public List<CompanyDto> getCompany() {
		return companyRepository.findAll().stream()
				.map(this::entityToDto)
				.collect(Collectors.toList());
	};

	public CompanyDto getCompanyById(String companyId) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
		return entityToDto(company);
	};

	public CompanyDto updateCompany(String companyId, CompanyDto companyDto) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
		
		if (companyDto.getCompanyName() != null) {
			company.setCompanyName(companyDto.getCompanyName());
		}
		if (companyDto.getBiZCode() != null) {
			company.setBiZCode(companyDto.getBiZCode());
		}
		if (companyDto.getAddressLine1() != null) {
			company.setAddressLine1(companyDto.getAddressLine1());
		}
		if (companyDto.getAddressLine2() != null) {
			company.setAddressLine2(companyDto.getAddressLine2());
		}
		if (companyDto.getPostalCode() != null) {
			company.setPostalCode(companyDto.getPostalCode());
		}
		if (companyDto.getCity() != null) {
			company.setCity(companyDto.getCity());
		}
		if (companyDto.getShowCompany() != null) {
			company.setShowCompany(companyDto.getShowCompany());
		}
		if (companyDto.getActive() != null) {
			company.setActive(companyDto.getActive());
		}
		if (companyDto.getLanguage() != null) {
			company.setLanguage(companyDto.getLanguage());
		}
		
		Company updatedCompany = companyRepository.save(company);
		return entityToDto(updatedCompany);
	};

	public void deleteCompany(String companyId) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
		companyRepository.delete(company);
	};

	// Helper methods for DTO mapping
	private CompanyDto entityToDto(Company company) {
		CompanyDto dto = new CompanyDto();
		dto.setCompanyId(company.getCompanyId());
		dto.setCompanyName(company.getCompanyName());
		dto.setBiZCode(company.getBiZCode());
		dto.setAddressLine1(company.getAddressLine1());
		dto.setAddressLine2(company.getAddressLine2());
		dto.setPostalCode(company.getPostalCode());
		dto.setCity(company.getCity());
		dto.setShowCompany(company.getShowCompany());
		dto.setActive(company.getActive());
		dto.setLanguage(company.getLanguage());
		return dto;
	}

	private Company dtoToEntity(CompanyDto dto) {
		Company company = new Company();
		company.setCompanyId(dto.getCompanyId());
		company.setCompanyName(dto.getCompanyName());
		company.setBiZCode(dto.getBiZCode());
		company.setAddressLine1(dto.getAddressLine1());
		company.setAddressLine2(dto.getAddressLine2());
		company.setPostalCode(dto.getPostalCode());
		company.setCity(dto.getCity());
		company.setShowCompany(dto.getShowCompany());
		company.setActive(dto.getActive());
		company.setLanguage(dto.getLanguage());
		return company;
	}
}
