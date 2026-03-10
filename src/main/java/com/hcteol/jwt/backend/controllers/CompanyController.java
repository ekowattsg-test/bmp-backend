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

import com.hcteol.jwt.backend.entities.Company;
import com.hcteol.jwt.backend.services.CompanyService;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	@PostMapping
	public ResponseEntity<Company> addCompany(@RequestBody Company company) {
		Company savedCompany = companyService.addCompany(company);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
	}

	@GetMapping
	public ResponseEntity<List<Company>> getCompanies() {
		List<Company> companies = companyService.getCompany();
		return ResponseEntity.ok(companies);
	}

	@GetMapping("/get/{companyId}")
	public ResponseEntity<Company> getCompanyById(@RequestBody String companyId) {
		Company company = companyService.getCompanyById(companyId);
		return ResponseEntity.ok(company);
	}

	@PutMapping("/{companyId}")
	public ResponseEntity<Company> updateCompany(@PathVariable String companyId, @RequestBody Company companyDetails) {
		try {
			Company updatedCompany = companyService.updateCompany(companyId, companyDetails);
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
