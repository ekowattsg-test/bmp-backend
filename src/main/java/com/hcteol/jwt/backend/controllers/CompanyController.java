package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hcteol.jwt.backend.dtos.CompanyDto;
import com.hcteol.jwt.backend.services.CompanyService;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	@PostMapping
	public ResponseEntity<CompanyDto> addCompany(@RequestBody CompanyDto companyDto) {
		CompanyDto savedCompany = companyService.addCompany(companyDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
	}

	@GetMapping
	public ResponseEntity<List<CompanyDto>> getCompanies() {
		List<CompanyDto> companies = companyService.getCompany();
		return ResponseEntity.ok(companies);
	}

	@GetMapping("/{companyId}")
	public ResponseEntity<CompanyDto> getCompanyById(@PathVariable String companyId) {
		try {
			CompanyDto company = companyService.getCompanyById(companyId);
			return ResponseEntity.ok(company);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PutMapping("/{companyId}")
	public ResponseEntity<CompanyDto> updateCompany(@PathVariable String companyId, @RequestBody CompanyDto companyDto) {
		try {
			CompanyDto updatedCompany = companyService.updateCompany(companyId, companyDto);
			return ResponseEntity.ok(updatedCompany);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<Void> deleteCompany(@PathVariable String companyId) {
		try {
			companyService.deleteCompany(companyId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
