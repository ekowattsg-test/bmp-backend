package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectTaskProgress;

@Repository
public interface ProjectTaskProgressRepository extends JpaRepository<ProjectTaskProgress, Long> {

    List<ProjectTaskProgress> findByProjectTaskId(Long projectTaskId);

    Optional<ProjectTaskProgress> findByProjectTaskIdAndProgressDate(Long projectTaskId, String progressDate);

    List<ProjectTaskProgress> findByExecutedBy(String executedBy);

    List<ProjectTaskProgress> findByReportedBy(String reportedBy);

    List<ProjectTaskProgress> findByMarker(String marker);

    List<ProjectTaskProgress> findByProjectTaskIdAndMarker(Long projectTaskId, String marker);

    List<ProjectTaskProgress> findByCompleted(Integer completed);

    List<ProjectTaskProgress> findByProjectTaskIdAndCompleted(Long projectTaskId, Integer completed);
}
