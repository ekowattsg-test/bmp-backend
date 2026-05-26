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
    private String contentType;
    private String roleName;
    private Integer numberOfSteps;
    private Integer needDetails;
    private Integer active;
}
