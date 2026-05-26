package com.hcteol.jwt.backend.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductPurchaseStatsDto {

    private Long productId;
    private Double averageCost; // weighted by quantity
    private Double latestCost;
    private Double highestCost;
    private Double lowestCost;
    private Long totalQuantity;
    private List<com.hcteol.jwt.backend.dtos.VendorPurchaseSummaryDto> vendorSummaries;
}
