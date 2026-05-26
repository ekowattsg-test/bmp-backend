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
public class DeliveryOrderDto {

    private String orderId;
    private Long customerId;
    private String projectCode;
    private Date orderDate;
    private Double deliveryAmount;
    private String orderStatus;
    private Date issuedDate;
    private Date deliveryDate;
    private Date readyDate;
    private Date deliveredDate;
    private Date cancelledDate;
    private List<DeliveryOrderItemDto> items;
}
