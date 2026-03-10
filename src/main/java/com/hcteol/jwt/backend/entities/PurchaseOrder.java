package com.hcteol.jwt.backend.entities;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class PurchaseOrder {
    @Id
    private String orderId;
    private Long vendorId;
    private Date orderDate;
    private Double purchaseAmount;
       @Column(columnDefinition = "varchar(20) default 'NEW'")
    private String orderStatus;


}