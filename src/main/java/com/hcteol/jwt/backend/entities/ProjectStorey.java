package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectStorey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectStoreyId;

    private Long projectBlockId;
    private String storeyName;
    private String storeyDescription;
    private Long storeyNumber;
    private String status;
}
