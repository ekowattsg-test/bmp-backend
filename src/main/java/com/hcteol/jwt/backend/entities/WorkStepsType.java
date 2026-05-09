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
}