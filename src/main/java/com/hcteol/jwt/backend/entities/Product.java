package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String productDescription;
    private String uom;
    @Column(nullable = false)
    private Long baselinedQuantity = 0L;
    private String baselinedDate = null;
    @Column(length = 1)
    @Pattern(regexp = "[AC]", message = "productCategory must be 'A' (Assets) or 'C' (Consumable)")
    private String productCategory;
    private String productClass = "General";
    private String productCode;
    @Column(length = 2000)
    private String productPicture;
}