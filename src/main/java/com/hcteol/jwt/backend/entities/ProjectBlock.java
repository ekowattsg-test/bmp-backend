package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectBlockId;

    private String projectCode;
    private String blockName;
    private String blockDescription;
    private Long blockNumber;
    private String status;
}
