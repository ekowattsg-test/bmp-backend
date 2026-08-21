package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TaskDeliveryRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskDeliveryRequirementId;

    private String projectCode;
    private Long projectTaskId;
    private Long activityId;
    private String activityName;
    private String inventoryType;
    private Long inventoryId;
    private Long productId;
    private String productCode;
    private String productName;
    private String productUom;
    private Long requiredQuantity;
    private Long availableQuantity;
    private Long deliveryQuantity;
    private Integer selected; // 0 = not selected, 1 = selected
    private String status; // EXTRACTED, SELECTED, GENERATED
    private String weekStartDate;
    private String extractionDate;
    private String deliveryOrderId;
    private String deliveryDate;
}
