package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectTaskId;
    private Long projectStreamId;
    private String taskType;    // "D" - dependent task, "A" - Anchor task, "B" - baseline task (Anchor and cannot be deleted)
    private String taskName;
    private String staffId; // person in-charge of task
    private String taskStartDate;
    private String taskEndDate;
}