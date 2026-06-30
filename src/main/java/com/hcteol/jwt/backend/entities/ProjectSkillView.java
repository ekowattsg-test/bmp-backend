package com.hcteol.jwt.backend.entities;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Immutable
@Subselect("select row_number() over (order by v.project_code, v.project_stream_id, v.project_task_id, v.staff_skill_id, v.skill_name) as row_id, v.* from project_skill_view v")
@Synchronize({"project_skill", "project_task", "project_stream", "project", "staff_skill"})
public class ProjectSkillView {

    @Id
    @Column(name = "row_id")
    private Long rowId;

    @Column(name = "project_code")
    private String projectCode;

    private Integer active;

    private String status;

    @Column(name = "project_stream_id")
    private Long projectStreamId;

    @Column(name = "stream_name")
    private String streamName;

    @Column(name = "project_task_id")
    private Long projectTaskId;

    @Column(name = "task_status")
    private String taskStatus;

    @Column(name = "task_start_date")
    private String taskStartDate;

    @Column(name = "task_end_date")
    private String taskEndDate;

    @Column(name = "actual_start_date")
    private String actualStartDate;

    @Column(name = "actual_end_date")
    private String actualEndDate;

    @Column(name = "staff_skill_id")
    private Long staffSkillId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "skill_category")
    private String skillCategory;

    @Column(name = "skill_description")
    private String skillDescription;

    private Integer unit;
}
