package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
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
    private String taskType;
    private String taskName;
    private String mobileNumber; // person in-charge of task
    private String taskStartDate;
    private String taskEndDate;
}