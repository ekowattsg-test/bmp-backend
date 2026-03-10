package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Vendor {
    @Id
    private Long vendorId;
    private String vendorName;
    private Boolean active;
    private String contactEmail;
    private String latitude;
    private String longitude;
}
