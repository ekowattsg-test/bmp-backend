package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WorkSteps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workStepsId;
    private Long workOrderId;
    private Integer stepNumber;
    private String fromLocation;
    private String toLocation;
    private String stepStatus;
}
