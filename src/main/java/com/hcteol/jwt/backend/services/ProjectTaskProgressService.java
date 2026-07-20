package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.ProjectTaskProgressUpdateResponse;
import com.hcteol.jwt.backend.entities.ProjectTaskProgress;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskProgressRepository;

@Service
public class ProjectTaskProgressService {

    @Autowired
    private ProjectTaskProgressRepository projectTaskProgressRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Autowired
    private ProjectTaskRecalculationService projectTaskRecalculationService;

    private String normalizeMarker(String marker) {
        if (marker == null || marker.isBlank()) {
            return null;
        }
        String normalized = marker.trim().toUpperCase();
        if (!"M".equals(normalized) && !"C".equals(normalized) && !"U".equals(normalized)) {
            throw new IllegalArgumentException("marker must be either M, C, or U");
        }
        return normalized;
    }

    private boolean isTaskScheduleChanged(ProjectTask before, ProjectTask after) {
        return !Objects.equals(before.getTaskStartDate(), after.getTaskStartDate())
                || !Objects.equals(before.getTaskEndDate(), after.getTaskEndDate())
                || !Objects.equals(before.getActualStartDate(), after.getActualStartDate())
                || !Objects.equals(before.getActualEndDate(), after.getActualEndDate())
                || !Objects.equals(before.getTaskStatus(), after.getTaskStatus());
    }

    private ProjectTask syncProjectTaskFromProgress(ProjectTaskProgress progressRecord, boolean fromUpdate) {
        Long projectTaskId = progressRecord.getProjectTaskId();
        if (projectTaskId == null) {
            return null;
        }

        return projectTaskRepository.findById(projectTaskId).map(projectTask -> {
            ProjectTask before = new ProjectTask();
            before.setTaskStartDate(projectTask.getTaskStartDate());
            before.setTaskEndDate(projectTask.getTaskEndDate());
            before.setActualStartDate(projectTask.getActualStartDate());
            before.setActualEndDate(projectTask.getActualEndDate());
            before.setTaskStatus(projectTask.getTaskStatus());

            projectTask.setProgress(progressRecord.getProgress());

            String currentStatus = projectTask.getTaskStatus() == null
                    ? ""
                    : projectTask.getTaskStatus().trim().toLowerCase();
            if (fromUpdate && "C".equals(progressRecord.getMarker()) && "not started".equals(currentStatus)) {
                projectTask.setTaskStatus("In Progress");
                projectTask.setActualStartDate(progressRecord.getProgressDate());
            }
            if (fromUpdate && Integer.valueOf(1).equals(progressRecord.getCompleted())) {
                projectTask.setTaskStatus("Completed");
                projectTask.setActualEndDate(progressRecord.getProgressDate());
            }

            boolean scheduleChanged = isTaskScheduleChanged(before, projectTask);
            ProjectTask savedTask = projectTaskRepository.save(projectTask);
            ProjectTask latestTask = savedTask;

            if (scheduleChanged && savedTask.getProjectTaskId() != null) {
                ProjectTask recalculatedTask = projectTaskDateCalculationService.calculateTaskDates(savedTask);
                projectTaskRepository.save(recalculatedTask);
                projectTaskRecalculationService.recalculateAfterTaskChange(savedTask.getProjectTaskId());
                latestTask = projectTaskRepository.findById(savedTask.getProjectTaskId()).orElse(recalculatedTask);
            }

            return latestTask;
        }).orElse(null);
    }

    @Transactional
    public ProjectTaskProgress addProjectTaskProgress(ProjectTaskProgress projectTaskProgress) {
        ProjectTaskProgress target = Objects.requireNonNull(projectTaskProgress, "projectTaskProgress cannot be null");
        target.setMarker(normalizeMarker(target.getMarker()));
        target.setReportedBy(null);
        ProjectTaskProgress savedProgress = projectTaskProgressRepository.save(target);
        syncProjectTaskFromProgress(savedProgress, false);
        return savedProgress;
    }

    public List<ProjectTaskProgress> getAllProjectTaskProgresses() {
        return projectTaskProgressRepository.findAll();
    }

    public Optional<ProjectTaskProgress> getProjectTaskProgressById(Long id) {
        return projectTaskProgressRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByProjectTaskId(Long projectTaskId) {
        return projectTaskProgressRepository
                .findByProjectTaskId(Objects.requireNonNull(projectTaskId, "projectTaskId cannot be null"));
    }

    public Optional<ProjectTaskProgress> getProjectTaskProgressByProjectTaskIdAndProgressDate(Long projectTaskId,
            String progressDate) {
        return projectTaskProgressRepository.findByProjectTaskIdAndProgressDate(
                Objects.requireNonNull(projectTaskId, "projectTaskId cannot be null"),
                Objects.requireNonNull(progressDate, "progressDate cannot be null"));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByExecutedBy(String executedBy) {
        return projectTaskProgressRepository.findByExecutedBy(Objects.requireNonNull(executedBy, "executedBy cannot be null"));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByReportedBy(String reportedBy) {
        return projectTaskProgressRepository.findByReportedBy(Objects.requireNonNull(reportedBy, "reportedBy cannot be null"));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByMarker(String marker) {
        return projectTaskProgressRepository.findByMarker(normalizeMarker(marker));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByProjectTaskIdAndMarker(Long projectTaskId, String marker) {
        return projectTaskProgressRepository.findByProjectTaskIdAndMarker(
                Objects.requireNonNull(projectTaskId, "projectTaskId cannot be null"),
                normalizeMarker(marker));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByCompleted(Integer completed) {
        return projectTaskProgressRepository
                .findByCompleted(Objects.requireNonNull(completed, "completed cannot be null"));
    }

    public List<ProjectTaskProgress> getProjectTaskProgressesByProjectTaskIdAndCompleted(Long projectTaskId,
            Integer completed) {
        return projectTaskProgressRepository.findByProjectTaskIdAndCompleted(
                Objects.requireNonNull(projectTaskId, "projectTaskId cannot be null"),
                Objects.requireNonNull(completed, "completed cannot be null"));
    }

    @Transactional
    public ProjectTaskProgress updateProjectTaskProgress(Long id, ProjectTaskProgress details) {
        ProjectTaskProgress existing = projectTaskProgressRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing,
                "projectTaskProgressId");
        existing.setMarker(normalizeMarker(existing.getMarker()));
        ProjectTaskProgress savedProgress = projectTaskProgressRepository.save(existing);
        syncProjectTaskFromProgress(savedProgress, true);
        return savedProgress;
    }

    @Transactional
    public ProjectTaskProgressUpdateResponse updateProjectTaskProgressWithTaskSnapshot(Long id, ProjectTaskProgress details) {
        ProjectTaskProgress existing = projectTaskProgressRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing,
                "projectTaskProgressId");
        existing.setMarker(normalizeMarker(existing.getMarker()));
        ProjectTaskProgress savedProgress = projectTaskProgressRepository.save(existing);
        ProjectTask updatedTaskSnapshot = syncProjectTaskFromProgress(savedProgress, true);
        return new ProjectTaskProgressUpdateResponse(savedProgress, updatedTaskSnapshot);
    }

    public void deleteProjectTaskProgress(Long id) {
        projectTaskProgressRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
