package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class WorkOrder {

    @Id
    private String workOrderId;
    private String workOrderType;
    private String workDescription;
    private String issuedBy;
    private LocalDateTime workOrderDate;
    private String workBy;  // mobile number of the staff to do the job
    private String workOrderStatus;
}
