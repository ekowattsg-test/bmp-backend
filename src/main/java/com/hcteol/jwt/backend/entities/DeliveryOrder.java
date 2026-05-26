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
public class DeliveryOrder {

    @Id
    private String orderId;
    private Long customerId;
    private String projectCode;
    private Date orderDate;
    private Double deliveryAmount;
    @Column(columnDefinition = "varchar(20) default 'NEW'")
    private Date deliveryDate;
    private String orderStatus;
    private Date readyDate;
    private Date issuedDate;
    private Date deliveredDate;
    private Date cancelledDate;
}
