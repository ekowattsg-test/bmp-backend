package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectTask;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProjectStreamId(Long projectStreamId);

    List<ProjectTask> findByParentTaskId(Long parentTaskId);

    List<ProjectTask> findByMilestoneTaskId(Long milestoneTaskId);
}
