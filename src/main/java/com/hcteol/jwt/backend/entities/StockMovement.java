package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;

@Data
@Entity
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;
    private Long stockId;
    @Column(length = 1)
    @Size(max = 1, message = "movementType must be a single character")
    private String movementType;
    private Integer quantity;
    private String location = "central";
    private String reference;
    private String recordDate;
    private String actionBy;
}