package com.hcteol.jwt.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String companyId;
    private String companyName;
    private String biZCode;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private String city;
    private Boolean showCompany;
    private Boolean active;
    private String language;
}
