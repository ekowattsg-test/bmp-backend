package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectManpower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectManpowerId;
    private Long projectTaskId;
    private Long projectSkillId;
    private String workDate;
    private String staffId; // staff assigned to task
    private Double loading = 1.0; // percentage of staff workload needed in task
}
