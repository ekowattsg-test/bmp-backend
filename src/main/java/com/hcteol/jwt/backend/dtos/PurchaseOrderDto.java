package com.hcteol.jwt.backend.dtos;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PurchaseOrderDto {

    private String orderId;
    private Long vendorId;
    private Date orderDate;
    private Double purchaseAmount;
    private String orderStatus;
    private Date issuedDate;
    private Date confirmedDate;
    private Date readyDate;
    private Date receivedDate;
    private Date cancelledDate;
    private List<PurchaseOrderItemDto> items;
}
