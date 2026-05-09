package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WorkOrderType {

    @Id
    private String workOrderType;
    private String workOrderDescription;
    private String contentType; // stock or worker
    private Integer numberOfSteps;
    private Integer active;
}
