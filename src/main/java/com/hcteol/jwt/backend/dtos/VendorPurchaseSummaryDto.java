package com.hcteol.jwt.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VendorPurchaseSummaryDto {

    private Long vendorId;
    private Double averageCost;
    private Double highestCost;
    private Double lowestCost;
    private Long totalQuantity;
}
