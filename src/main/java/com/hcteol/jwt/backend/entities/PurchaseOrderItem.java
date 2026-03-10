package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class PurchaseOrderItem {
    @Id
    private String itemId;
    private String orderId;
    @Column(length = 1)
    @Pattern(regexp = "[AI]", message = "itemType must be either 'A' or 'I'")
    private String itemType;
    private String productCode;
    private String internalProductCode;
    private Long internalOrderId;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
}