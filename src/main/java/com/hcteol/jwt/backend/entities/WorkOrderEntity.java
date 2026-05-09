package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WorkOrderEntity {
    @Id
    private String workOrderEntity;
    private String description;
}
