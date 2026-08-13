package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectUnitId;

    private Long projectStoreyId;
    private Long projectStackId;
    private String unitName;
    private String unitDescription;
    private Long unitNumber;
    private Long projectStreamId;
    private String status;
}
