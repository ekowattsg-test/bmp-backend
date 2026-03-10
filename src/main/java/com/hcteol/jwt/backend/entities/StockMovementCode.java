package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class StockMovementCode {
    @Id
    @Column(length = 1)
    @Size(max = 1, message = "movementType must be a single character")
    private String movementType;
    private String movementDescription;
    @Column(nullable = false)
    private Integer stockModifier = 0;
    @Column(nullable = false)
    private Integer holdModifier = 0;
}