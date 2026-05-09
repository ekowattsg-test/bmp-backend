package com.hcteol.jwt.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workOrderId;
    private String workOrderType;
    private String workDescription;
    private String issuedBy;
    private LocalDateTime workOrderDate;
    private String workBy;  // mobile number of the staff to do the job
    private String workOrderStatus;
}
