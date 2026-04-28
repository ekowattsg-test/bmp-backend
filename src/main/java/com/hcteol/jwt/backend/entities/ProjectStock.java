package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectStockId;
    private Long projectTaskId;
    private Long productId;
    private Double quantity;
}