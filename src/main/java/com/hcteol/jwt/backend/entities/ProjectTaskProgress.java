package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProjectTaskProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectTaskProgressId;
    private Long projectTaskId;
    private String progressDate; // date when the progress was recorded
    private String executedBy; // staff Id of person who executed the day's task
    private Integer progress; // task progress percentage, 0-100 
    private Integer completed;
    private String reportedBy; // staff Id of person who reported the day's task progress
    private String marker; // M - Marked task for manpower planning, C - Confirmed task, U - progress updated
    private String inspectionDate; // date when the progress was inspected
    private String inspectedBy; // staff Id of person who inspected the progress
    private Integer reportedProgress; // progress percentage reported for inspection, 0-100
    private Integer verifiedProgress; // progress percentage verified during inspection, 0-100
    @Column(length = 2000)
    private String inspectionPhotos; // inspection photo URLs as comma-separated values
    @Column(columnDefinition = "TEXT")
    private String inspectionRemark;
}
