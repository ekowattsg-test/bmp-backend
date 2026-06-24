package com.hcteol.jwt.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectStreamDateRecalculationService {

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public void recalculateStreamDatesForProject(String projectCode) {
        List<ProjectStream> streams = projectStreamRepository.findByProjectCode(projectCode);
        for (ProjectStream stream : streams) {
            recalculateStreamDatesFromTasks(stream.getProjectStreamId());
        }
    }

    public void recalculateStreamDatesFromTasks(Long projectStreamId) {
        if (projectStreamId == null) {
            return;
        }

        Optional<ProjectStream> streamOptional = projectStreamRepository.findById(projectStreamId);
        if (streamOptional.isEmpty()) {
            return;
        }

        ProjectStream stream = streamOptional.get();
        List<ProjectTask> tasks = projectTaskRepository.findByProjectStreamId(projectStreamId);
        if (tasks.isEmpty()) {
            return;
        }

        String earliestStartStr = null;
        Date earliestStart = null;
        String latestEndStr = null;
        Date latestEnd = null;

        for (ProjectTask task : tasks) {
            String taskStartStr = resolveEffectiveTaskStartDate(task);
            String taskEndStr = resolveEffectiveTaskEndDate(task);
            Date taskStart = parseProjectDate(taskStartStr);
            Date taskEnd = parseProjectDate(taskEndStr);

            if (taskStart != null && (earliestStart == null || taskStart.before(earliestStart))) {
                earliestStart = taskStart;
                earliestStartStr = taskStartStr;
            }

            if (taskEnd != null && (latestEnd == null || taskEnd.after(latestEnd))) {
                latestEnd = taskEnd;
                latestEndStr = taskEndStr;
            }
        }

        boolean changed = false;
        if (earliestStartStr != null) {
            stream.setStreamStartDate(earliestStartStr);
            changed = true;
        }
        if (latestEndStr != null) {
            stream.setStreamEndDate(latestEndStr);
            changed = true;
        }

        if (changed) {
            projectStreamRepository.save(stream);
        }
    }

    private String resolveEffectiveTaskStartDate(ProjectTask task) {
        if (task == null) {
            return null;
        }

        String status = normalizeStatus(task.getTaskStatus());
        if ("not started".equals(status)) {
            return task.getTaskStartDate();
        }
        return task.getActualStartDate();
    }

    private String resolveEffectiveTaskEndDate(ProjectTask task) {
        if (task == null) {
            return null;
        }

        String status = normalizeStatus(task.getTaskStatus());
        if ("completed".equals(status)) {
            return task.getActualEndDate();
        }
        return task.getTaskEndDate();
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase();
    }

    private Date parseProjectDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String v = value.trim();
        try {
            return Date.from(Instant.parse(v));
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate localDate = LocalDate.parse(v);
                return java.sql.Date.valueOf(localDate);
            } catch (DateTimeParseException ignoredDateOnly) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(v);
                    return java.sql.Timestamp.valueOf(localDateTime);
                } catch (DateTimeParseException ignoredDateTime) {
                    return null;
                }
            }
        }
    }
}
