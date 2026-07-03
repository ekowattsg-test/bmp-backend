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
    private Long parentTaskId; // for dependent tasks, reference to the parent task
    private Long milestoneTaskId;
    private Long taskDuration; // in days, used to calculate start and end date
    private String taskStartDate;
    private String taskEndDate;
    private String taskStatus; // "Not Started", "In Progress", "Completed"
    private String actualStartDate;
    private String actualEndDate;
    private Integer manpowerTouched = 0; // 1 - manpower manually adjusted, 0 - manpower not adjusted
    private String remarks;
}
