package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RequisitionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requisitionOrderId;
    private Long requisitionCycleId;
    private String projectCode;
    private String requisitionDate;
    private Long productRequested;
    private Long quantityRequested;
    private Long vendorSuggested;
    private Double priceSuggested;
    private Long productPurchased;
    private Long quantityPurchased;
    private Long vendorPurchased;
    private Double unitPrice;
    private Integer selected; // 0 = not selected, 1 = selected
    private String purchaseOrderId;
    private String purchaseDate;
    private String status; // requisited, selected, created, approved

}
