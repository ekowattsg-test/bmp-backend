package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Customer {
    @Id
    private Long customerId;
    private String customerName;
    private Boolean active;
    private String contactEmail;
    private String latitude;
    private String longitude;
    
}
