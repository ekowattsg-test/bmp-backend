package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class WorkSteps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workStepsId;
    private String workOrderId;
    private Integer stepNumber;
    private String fromLocation;
    private String toLocation;
    @Column(length = 2000)
    private String photos; // store photo URLs as comma-separated string (max 2000 chars)
    private String stepStatus;
}
