package com.hcteol.jwt.backend.services;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.entities.ProjectTaskType;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskTypeRepository;

@Service
public class ProjectTaskDateCalculationService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectTaskTypeRepository projectTaskTypeRepository;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private ProjectTaskDependencyService projectTaskDependencyService;

    public ProjectTask calculateTaskDates(ProjectTask inputTask) {
        if (inputTask == null) {
            throw new IllegalArgumentException("projectTask is required");
        }
        if (inputTask.getTaskType() == null || inputTask.getTaskType().isBlank()) {
            throw new IllegalArgumentException("taskType is required");
        }

        projectTaskDependencyService.validateNoDependencyCycle(inputTask);

        ProjectTaskType taskType = resolveTaskType(inputTask.getTaskType());

        String alignWith = taskType.getAlignWith();
        if (alignWith == null || alignWith.isBlank()) {
            return inputTask;
        }

        String normalized = alignWith.trim().toLowerCase();
        if ("no".equals(normalized)) {
            return inputTask;
        }

        Long durationObj = inputTask.getTaskDuration();
        long duration = durationObj != null ? durationObj : 1L;
        if (duration < 0) {
            throw new IllegalArgumentException("taskDuration must be >= 0");
        }

        int workDaysPerWeek = resolveWorkDaysPerWeek();

        switch (normalized) {
            case "latest" -> {
                LocalDate start = parseToLocalDate(inputTask.getTaskStartDate(), "taskStartDate");
                String date = start.toString();
                inputTask.setTaskStartDate(date);
                inputTask.setTaskEndDate(date);
            }
            case "anywhere" -> {
                LocalDate start = parseToLocalDate(inputTask.getTaskStartDate(), "taskStartDate");
                inputTask.setTaskStartDate(start.toString());
                LocalDate end = addWorkingDays(start, duration - 1, workDaysPerWeek);
                inputTask.setTaskEndDate(end.toString());
            }
            case "start-start" -> {
                ProjectTask parentTask = resolveParentTask(inputTask.getParentTaskId(), alignWith);
                LocalDate start = parseToLocalDate(parentTask.getTaskStartDate(), "parentTask.taskStartDate");
                inputTask.setTaskStartDate(start.toString());
                LocalDate end = addWorkingDays(start, duration - 1, workDaysPerWeek);
                inputTask.setTaskEndDate(end.toString());
            }
            case "end-end" -> {
                ProjectTask parentTask = resolveParentTask(inputTask.getParentTaskId(), alignWith);
                LocalDate end = parseToLocalDate(parentTask.getTaskEndDate(), "parentTask.taskEndDate");
                inputTask.setTaskEndDate(end.toString());
                LocalDate start = addWorkingDays(end, -(duration + 1), workDaysPerWeek);
                inputTask.setTaskStartDate(start.toString());
            }
            case "end-start" -> {
                ProjectTask parentTask = resolveParentTask(inputTask.getParentTaskId(), alignWith);
                LocalDate parentEnd = parseToLocalDate(parentTask.getTaskEndDate(), "parentTask.taskEndDate");
                LocalDate start = addWorkingDays(parentEnd, 1, workDaysPerWeek);
                inputTask.setTaskStartDate(start.toString());
                LocalDate end = addWorkingDays(start, duration - 1, workDaysPerWeek);
                inputTask.setTaskEndDate(end.toString());
            }
            default -> {
                return inputTask;
            }
        }

        return inputTask;
    }

    private ProjectTaskType resolveTaskType(String taskTypeCode) {
        String normalized = taskTypeCode == null ? "" : taskTypeCode.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("taskType is required");
        }

        Optional<ProjectTaskType> exact = projectTaskTypeRepository.findById(normalized);
        if (exact.isPresent()) {
            return exact.get();
        }

        Optional<ProjectTaskType> upper = projectTaskTypeRepository.findById(normalized.toUpperCase());
        if (upper.isPresent()) {
            return upper.get();
        }

        throw new IllegalArgumentException("ProjectTaskType not found for code " + taskTypeCode);
    }

    private ProjectTask resolveParentTask(Long parentTaskId, String alignWith) {
        if (parentTaskId == null) {
            throw new IllegalArgumentException("parentTaskId is required for alignWith " + alignWith);
        }

        return projectTaskRepository.findById(parentTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Parent task not found with id " + parentTaskId));
    }

    private int resolveWorkDaysPerWeek() {
        Optional<Param> param = paramRepository.findById("workDaysPerWeek");
        if (param.isEmpty() || param.get().getValue_string() == null) {
            return 7;
        }

        try {
            int parsed = Integer.parseInt(param.get().getValue_string().trim());
            if (parsed < 1) {
                return 1;
            }
            if (parsed > 7) {
                return 7;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return 7;
        }
    }

    private LocalDate parseToLocalDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        String v = value.trim();
        try {
            return Instant.parse(v).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(v);
            } catch (DateTimeParseException ignoredDateOnly) {
                try {
                    return LocalDateTime.parse(v).toLocalDate();
                } catch (DateTimeParseException ignoredDateTime) {
                    throw new IllegalArgumentException("Invalid date value in " + fieldName + ": " + value);
                }
            }
        }
    }

    private LocalDate addWorkingDays(LocalDate base, long workingDaysDelta, int workDaysPerWeek) {
        if (workingDaysDelta == 0) {
            return base;
        }

        LocalDate cursor = base;
        int direction = workingDaysDelta > 0 ? 1 : -1;
        long remaining = Math.abs(workingDaysDelta);
        while (remaining > 0) {
            cursor = cursor.plusDays(direction);
            if (isWorkingDay(cursor.getDayOfWeek(), workDaysPerWeek)) {
                remaining--;
            }
        }
        return cursor;
    }

    private boolean isWorkingDay(DayOfWeek dayOfWeek, int workDaysPerWeek) {
        int dayNumber = dayOfWeek.getValue();
        return dayNumber <= workDaysPerWeek;
    }
}
