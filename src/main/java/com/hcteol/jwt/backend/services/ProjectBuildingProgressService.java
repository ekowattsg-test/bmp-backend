package com.hcteol.jwt.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.dtos.ProjectBlockProgressDto;
import com.hcteol.jwt.backend.dtos.ProjectBuildingProgressResponse;
import com.hcteol.jwt.backend.dtos.ProjectStackProgressDto;
import com.hcteol.jwt.backend.dtos.ProjectStoreyProgressDto;
import com.hcteol.jwt.backend.dtos.ProjectUnitProgressDto;
import com.hcteol.jwt.backend.dtos.ProjectUnitWorkProgressDto;
import com.hcteol.jwt.backend.entities.ProjectBlock;
import com.hcteol.jwt.backend.entities.ProjectStack;
import com.hcteol.jwt.backend.entities.ProjectStorey;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.repositories.ProjectBlockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStackRepository;
import com.hcteol.jwt.backend.repositories.ProjectStoreyRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitRepository;

@Service
public class ProjectBuildingProgressService {

    @Autowired
    private ProjectBlockRepository projectBlockRepository;

    @Autowired
    private ProjectStoreyRepository projectStoreyRepository;

    @Autowired
    private ProjectStackRepository projectStackRepository;

    @Autowired
    private ProjectUnitRepository projectUnitRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectBuildingProgressResponse computeProgress(String projectCode) {
        ProjectBuildingProgressResponse response = new ProjectBuildingProgressResponse();
        response.setProjectCode(projectCode);

        List<ProjectBlockProgressDto> blockDtos = new ArrayList<>();
        List<ProjectBlock> blocks = projectBlockRepository.findByProjectCodeAndStatusOrderByBlockNumberAsc(projectCode, "ACTIVE");
        for (ProjectBlock block : blocks) {
            ProjectBlockProgressDto blockDto = new ProjectBlockProgressDto();
            blockDto.setProjectBlockId(block.getProjectBlockId());
            blockDto.setBlockName(block.getBlockName());
            blockDto.setBlockNumber(block.getBlockNumber());
            blockDto.setBlockDescription(block.getBlockDescription());
            blockDto.setStatus(block.getStatus());

            List<ProjectStackProgressDto> stackDtos = new ArrayList<>();
            Map<Long, ProjectStack> stackLookup = new HashMap<>();
            List<ProjectStack> stacks = projectStackRepository
                    .findByProjectBlockIdAndStatusOrderByStackNumberAsc(block.getProjectBlockId(), "ACTIVE");
            for (ProjectStack stack : stacks) {
                ProjectStackProgressDto stackDto = new ProjectStackProgressDto();
                stackDto.setProjectStackId(stack.getProjectStackId());
                stackDto.setStackName(stack.getStackName());
                stackDto.setStackNumber(stack.getStackNumber());
                stackDto.setStackDescription(stack.getStackDescription());
                stackDto.setStatus(stack.getStatus());
                stackDtos.add(stackDto);
                stackLookup.put(stack.getProjectStackId(), stack);
            }
            blockDto.setStacks(stackDtos);

            List<ProjectStoreyProgressDto> storeyDtos = new ArrayList<>();
            List<ProjectStorey> storeys = projectStoreyRepository.findByProjectBlockIdAndStatusOrderByStoreyNumberAsc(block.getProjectBlockId(), "ACTIVE");
            for (ProjectStorey storey : storeys) {
                ProjectStoreyProgressDto storeyDto = new ProjectStoreyProgressDto();
                storeyDto.setProjectStoreyId(storey.getProjectStoreyId());
                storeyDto.setStoreyName(storey.getStoreyName());
                storeyDto.setStoreyNumber(storey.getStoreyNumber());
                storeyDto.setStoreyDescription(storey.getStoreyDescription());
                storeyDto.setStatus(storey.getStatus());

                List<ProjectUnitProgressDto> unitDtos = new ArrayList<>();
                List<ProjectUnit> units = projectUnitRepository.findByProjectStoreyIdAndStatusOrderByUnitNumberAsc(storey.getProjectStoreyId(), "ACTIVE");
                for (ProjectUnit unit : units) {
                    unitDtos.add(buildUnitDto(unit, stackLookup));
                }

                storeyDto.setUnits(unitDtos);
                storeyDtos.add(storeyDto);
            }

            blockDto.setStoreys(storeyDtos);
            blockDtos.add(blockDto);
        }

        response.setBlocks(blockDtos);
        return response;
    }

    private ProjectUnitProgressDto buildUnitDto(ProjectUnit unit, Map<Long, ProjectStack> stackLookup) {
        ProjectUnitProgressDto unitDto = new ProjectUnitProgressDto();
        unitDto.setProjectUnitId(unit.getProjectUnitId());
        unitDto.setUnitName(unit.getUnitName());
        unitDto.setUnitNumber(unit.getUnitNumber());
        unitDto.setUnitDescription(unit.getUnitDescription());
        unitDto.setProjectStackId(unit.getProjectStackId());
        unitDto.setProjectStreamId(unit.getProjectStreamId());
        unitDto.setStatus(unit.getStatus());

        if (unit.getProjectStackId() != null) {
            ProjectStack stack = stackLookup.get(unit.getProjectStackId());
            if (stack != null) {
                unitDto.setStackName(stack.getStackName());
            }
        }

        List<ProjectUnitWorkProgressDto> workDtos = new ArrayList<>();
        List<ProjectTask> tasks = new ArrayList<>();
        Long projectStreamId = unit.getProjectStreamId();
        if (projectStreamId != null) {
            projectStreamRepository.findById(Objects.requireNonNull(projectStreamId, "projectStreamId cannot be null")).ifPresent(stream -> {
                unitDto.setStreamName(stream.getStreamName());
                Set<Long> streamIds = collectDescendantStreamIds(stream, new HashSet<>());
                for (Long streamId : streamIds) {
                    tasks.addAll(projectTaskRepository.findByProjectStreamId(streamId));
                }
            });
        }

        for (ProjectTask task : tasks) {
            workDtos.add(buildTaskWorkDto(task));
        }
        unitDto.setWorks(workDtos);

        if (tasks.isEmpty()) {
            unitDto.setProgress(0);
            return unitDto;
        }

        int totalProgress = 0;
        int taskCount = 0;
        DateRange plannedStart = null;
        DateRange plannedEnd = null;
        DateRange actualStart = null;
        DateRange actualEnd = null;

        for (ProjectTask task : tasks) {
            Integer taskProgress = task.getProgress();
            totalProgress += taskProgress == null ? 0 : taskProgress;
            taskCount++;
            plannedStart = chooseMinimum(plannedStart, task.getTaskStartDate());
            plannedEnd = chooseMaximum(plannedEnd, task.getTaskEndDate());
            actualStart = chooseMinimum(actualStart, task.getActualStartDate());
            actualEnd = chooseMaximum(actualEnd, task.getActualEndDate());
        }

        unitDto.setProgress(taskCount == 0 ? 0 : (int) Math.round((double) totalProgress / taskCount));
        unitDto.setPlannedStartDate(plannedStart == null ? null : plannedStart.rawValue());
        unitDto.setPlannedEndDate(plannedEnd == null ? null : plannedEnd.rawValue());
        unitDto.setActualStartDate(actualStart == null ? null : actualStart.rawValue());
        unitDto.setActualEndDate(actualEnd == null ? null : actualEnd.rawValue());
        return unitDto;
    }

    private Set<Long> collectDescendantStreamIds(ProjectStream stream, Set<Long> visited) {
        if (stream == null || stream.getProjectStreamId() == null || !visited.add(stream.getProjectStreamId())) {
            return visited;
        }
        List<ProjectStream> children = projectStreamRepository.findByProjectCodeAndParentStreamNumber(
                stream.getProjectCode(), stream.getStreamNumber());
        for (ProjectStream child : children) {
            collectDescendantStreamIds(child, visited);
        }
        return visited;
    }

    private ProjectUnitWorkProgressDto buildTaskWorkDto(ProjectTask task) {
        ProjectUnitWorkProgressDto workDto = new ProjectUnitWorkProgressDto();
        workDto.setProjectTaskId(task.getProjectTaskId());
        workDto.setWorkName(task.getTaskName());
        workDto.setStatus(task.getTaskStatus());
        Integer taskProgress = task.getProgress();
        workDto.setProgress(taskProgress == null ? 0 : taskProgress);
        workDto.setPlannedStartDate(task.getTaskStartDate());
        workDto.setPlannedEndDate(task.getTaskEndDate());
        workDto.setActualStartDate(task.getActualStartDate());
        workDto.setActualEndDate(task.getActualEndDate());
        return workDto;
    }

    private DateRange chooseMinimum(DateRange current, String candidateValue) {
        DateTimeValue candidate = parseDate(candidateValue);
        if (candidate == null) {
            return current;
        }
        if (current == null || candidate.instant().isBefore(current.instant())) {
            return new DateRange(candidate.instant(), candidate.rawValue());
        }
        return current;
    }

    private DateRange chooseMaximum(DateRange current, String candidateValue) {
        DateTimeValue candidate = parseDate(candidateValue);
        if (candidate == null) {
            return current;
        }
        if (current == null || candidate.instant().isAfter(current.instant())) {
            return new DateRange(candidate.instant(), candidate.rawValue());
        }
        return current;
    }

    private DateTimeValue parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String raw = value.trim();
        try {
            return new DateTimeValue(Instant.parse(raw), raw);
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate localDate = LocalDate.parse(raw);
                return new DateTimeValue(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), raw);
            } catch (DateTimeParseException ignoredDateOnly) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(raw);
                    return new DateTimeValue(localDateTime.atZone(ZoneId.systemDefault()).toInstant(), raw);
                } catch (DateTimeParseException ignoredDateTime) {
                    return null;
                }
            }
        }
    }

    private record DateTimeValue(Instant instant, String rawValue) {

    }

    private record DateRange(Instant instant, String rawValue) {

    }
}
