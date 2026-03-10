package com.hcteol.jwt.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PurchaseOrderItemDto {
    private String itemId;
    private String orderId;
    private String itemType;
    private String productCode;
    private String internalProductCode;
    private Long internalOrderId;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
}
