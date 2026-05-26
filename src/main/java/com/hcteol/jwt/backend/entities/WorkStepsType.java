package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WorkStepsType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workStepsTypeId;
    private String workOrderType;
    private Integer stepNumber;
    private String stepDescription;
    private String fromEntity;
    private String toEntity;
    private String startAction;
    private Integer scanData; // 0 or 1
    private Integer checkQuantity; // 0 or 1    
    private Integer newStock; // 0 or 1
    private Integer takePhoto; // 0 or 1
    private String endAction;
}
