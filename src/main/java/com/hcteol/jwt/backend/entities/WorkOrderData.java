package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WorkOrderData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workOrderDataId;
    private Long workOrderId;
    private Long productId;
    private Long quantity;
    private String staffId;
}