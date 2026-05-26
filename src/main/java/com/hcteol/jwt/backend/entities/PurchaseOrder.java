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
    private String projectCode;
    private Date orderDate;
    private Double purchaseAmount;
    @Column(name = "order_status", length = 20)
    @org.hibernate.annotations.ColumnDefault("'NEW'")
    private String orderStatus;
    private Date issuedDate;
    private Date confirmedDate;
    private Date readyDate;
    private Date receivedDate;
    private Date cancelledDate;
}
