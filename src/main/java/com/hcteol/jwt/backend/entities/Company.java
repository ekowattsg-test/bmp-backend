package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Company {
	@Id
	private String companyId;
	private String companyName;
	private Boolean showCompany;
	private Boolean active;
}
