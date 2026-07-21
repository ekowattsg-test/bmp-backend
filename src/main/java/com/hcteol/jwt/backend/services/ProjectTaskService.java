package com.hcteol.jwt.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Autowired
    private ProjectTaskRecalculationService projectTaskRecalculationService;

    @Autowired
    private ProjectStreamDateRecalculationService projectStreamDateRecalculationService;

    public List<ProjectTask> getAllProjectTasks() {
        return projectTaskRepository.findAll();
    }

    public Optional<ProjectTask> getProjectTaskById(Long id) {
        return projectTaskRepository.findById(id);
    }

    public List<ProjectTask> getProjectTasksByStreamId(Long projectStreamId) {
        return projectTaskRepository.findByProjectStreamId(projectStreamId);
    }

    @Transactional
    public ProjectTask createProjectTask(ProjectTask projectTask) {
        ProjectTask savedTask = projectTaskRepository.save(projectTask);
        projectTaskRepository.flush();
        projectTaskRecalculationService.recalculateAfterTaskChange(savedTask.getProjectTaskId());
        ProjectTask refreshedTask = projectTaskRepository.findById(savedTask.getProjectTaskId()).orElse(savedTask);
        projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(refreshedTask.getProjectStreamId());
        return refreshedTask;
    }

    public ProjectTask calculateProjectTask(ProjectTask inputTask) {
        ProjectTask calculatedTask = projectTaskDateCalculationService.calculateTaskDates(inputTask);
        projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(calculatedTask.getProjectStreamId());
        return calculatedTask;
    }

    @Transactional
    public ProjectTask updateProjectTask(Long id, ProjectTask projectTaskDetails) {
        return projectTaskRepository.findById(id).map(projectTask -> {
            String originalActualStartDate = projectTask.getActualStartDate();
            Long originalStreamId = projectTask.getProjectStreamId();
            projectTask.setProjectStreamId(projectTaskDetails.getProjectStreamId());
            projectTask.setTaskType(projectTaskDetails.getTaskType());
            projectTask.setTaskName(projectTaskDetails.getTaskName());
            projectTask.setStaffId(projectTaskDetails.getStaffId());
            projectTask.setParentTaskId(projectTaskDetails.getParentTaskId());
            projectTask.setMilestoneTaskId(projectTaskDetails.getMilestoneTaskId());
            projectTask.setTaskDuration(projectTaskDetails.getTaskDuration());
            projectTask.setTaskStartDate(projectTaskDetails.getTaskStartDate());
            projectTask.setTaskEndDate(projectTaskDetails.getTaskEndDate());
            projectTask.setTaskStatus(projectTaskDetails.getTaskStatus());
            projectTask.setProgress(projectTaskDetails.getProgress());
            projectTask.setActualStartDate(projectTaskDetails.getActualStartDate());
            projectTask.setActualEndDate(projectTaskDetails.getActualEndDate());
            projectTask.setRemarks(projectTaskDetails.getRemarks());
            ProjectTask savedTask = projectTaskRepository.save(projectTask);
            projectTaskRepository.flush();
            projectTaskRecalculationService.recalculateAfterTaskChange(savedTask.getProjectTaskId());

            ProjectTask refreshedTask = projectTaskRepository.findById(savedTask.getProjectTaskId()).orElse(savedTask);
            Long updatedStreamId = refreshedTask.getProjectStreamId();
            projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(originalStreamId);
            if (updatedStreamId != null && !updatedStreamId.equals(originalStreamId)) {
                projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(updatedStreamId);
            }

            boolean actualStartDateUpdated = !Objects.equals(originalActualStartDate,
                    projectTaskDetails.getActualStartDate());
            if (actualStartDateUpdated) {
                activateProjectAndRefreshProjectStartDateFromInProgressTasks(refreshedTask);
            }

            return refreshedTask;
        }).orElseThrow(() -> new RuntimeException("ProjectTask not found with id " + id));
    }

    private void activateProjectAndRefreshProjectStartDateFromInProgressTasks(ProjectTask task) {
        if (task == null || task.getProjectStreamId() == null) {
            return;
        }

        Optional<ProjectStream> streamOptional = projectStreamRepository.findById(task.getProjectStreamId());
        if (streamOptional.isEmpty()) {
            return;
        }

        String projectCode = streamOptional.get().getProjectCode();
        if (projectCode == null || projectCode.isBlank()) {
            return;
        }

        projectRepository.findById(projectCode).ifPresent(project -> {
            boolean changed = false;

            if (project.getStatus() == null || !"active".equalsIgnoreCase(project.getStatus().trim())) {
                project.setStatus("Active");
                changed = true;
            }

            String earliestActualStartDate = resolveEarliestActualStartDateForInProgressTasks(projectCode);
            if (earliestActualStartDate != null && !earliestActualStartDate.equals(project.getStartDate())) {
                project.setStartDate(earliestActualStartDate);
                changed = true;
            }

            if (changed) {
                projectRepository.save(project);
            }
        });
    }

    private String resolveEarliestActualStartDateForInProgressTasks(String projectCode) {
        List<ProjectStream> streams = projectStreamRepository.findByProjectCode(projectCode);

        LocalDate earliestDate = null;
        String earliestDateRaw = null;

        for (ProjectStream stream : streams) {
            if (stream.getProjectStreamId() == null) {
                continue;
            }

            List<ProjectTask> tasks = projectTaskRepository.findByProjectStreamId(stream.getProjectStreamId());
            for (ProjectTask task : tasks) {
                if (!"in progress".equalsIgnoreCase(normalizeStatus(task.getTaskStatus()))) {
                    continue;
                }

                String actualStartDate = task.getActualStartDate();
                LocalDate parsed = parseToLocalDate(actualStartDate);
                if (parsed == null) {
                    continue;
                }

                if (earliestDate == null || parsed.isBefore(earliestDate)) {
                    earliestDate = parsed;
                    earliestDateRaw = actualStartDate;
                }
            }
        }

        return earliestDateRaw;
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase();
    }

    private LocalDate parseToLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        try {
            return Instant.parse(trimmed).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(trimmed);
            } catch (DateTimeParseException ignoredDateOnly) {
                try {
                    return LocalDateTime.parse(trimmed).toLocalDate();
                } catch (DateTimeParseException ignoredDateTime) {
                    return null;
                }
            }
        }
    }

    public void deleteProjectTask(Long id) {
        Long streamId = projectTaskRepository.findById(id)
                .map(ProjectTask::getProjectStreamId)
                .orElse(null);
        projectTaskRepository.deleteById(id);
        projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(streamId);
    }

    @Transactional
    public void recalculateProjectTasksByProjectCode(String projectCode) {
        projectTaskRecalculationService.recalculateProjectByProjectCode(projectCode);
    }

}
