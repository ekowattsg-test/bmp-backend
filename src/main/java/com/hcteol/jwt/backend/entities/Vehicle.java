package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Vehicle {

    @Id
    private String vehicleNumber;
    private String driver; // mobile number of default driver
    private Integer active;
}
