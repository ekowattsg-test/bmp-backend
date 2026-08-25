package com.hcteol.jwt.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskRecalculationService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Autowired
    private ProjectTaskDependencyService projectTaskDependencyService;

    @Autowired
    private ProjectStreamDateRecalculationService projectStreamDateRecalculationService;

    @Transactional
    public void recalculateAfterTaskChange(Long changedTaskId) {
        if (changedTaskId == null) {
            return;
        }

        ProjectTask changedTask = projectTaskRepository.findById(changedTaskId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectTask not found with id " + changedTaskId));

        validateDependencyChainsUpFront(changedTaskId);

        Set<Long> branchVisited = new HashSet<>();
        Set<Long> processedMilestones = new HashSet<>();
        Set<Long> affectedStreamIds = new HashSet<>();

        if (changedTask.getProjectStreamId() != null) {
            affectedStreamIds.add(changedTask.getProjectStreamId());
        }

        if (!projectTaskRepository.findByMilestoneTaskId(changedTaskId).isEmpty()) {
            processedMilestones.add(changedTaskId);
            recalculateMilestoneDate(changedTaskId, affectedStreamIds);
            recalculateDependentBranch(changedTaskId, branchVisited, processedMilestones, affectedStreamIds);
        }

        processLinkedMilestone(changedTask, branchVisited, processedMilestones, affectedStreamIds);
        recalculateDependentBranch(changedTaskId, branchVisited, processedMilestones, affectedStreamIds);

        Set<Long> processedStreamIds = new HashSet<>();
        for (Long streamId : affectedStreamIds) {
            recalculateStreamAndAncestors(streamId, processedStreamIds);
        }
    }

    private void recalculateStreamAndAncestors(Long streamId, Set<Long> processedStreamIds) {
        if (streamId == null || !processedStreamIds.add(streamId)) {
            return;
        }

        projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(streamId);

        Optional<ProjectStream> streamOptional = projectStreamRepository.findById(streamId);
        if (streamOptional.isEmpty()) {
            return;
        }

        ProjectStream stream = streamOptional.get();
        Long parentStreamNumber = stream.getParentStreamNumber();
        if (parentStreamNumber == null) {
            return;
        }

        List<ProjectStream> parents = projectStreamRepository
                .findByProjectCodeAndStreamNumber(stream.getProjectCode(), parentStreamNumber);
        for (ProjectStream parent : parents) {
            Long parentId = parent.getProjectStreamId();
            if (parentId != null) {
                recalculateStreamAndAncestors(parentId, processedStreamIds);
            }
        }
    }

    @Transactional
    public void recalculateProjectByProjectCode(String projectCode) {
        if (projectCode == null || projectCode.isBlank()) {
            throw new IllegalArgumentException("projectCode is required");
        }

        List<ProjectStream> streams = projectStreamRepository.findByProjectCode(projectCode.trim());
        for (ProjectStream stream : streams) {
            Long streamId = stream.getProjectStreamId();
            if (streamId == null) {
                continue;
            }

            List<ProjectTask> tasks = projectTaskRepository.findByProjectStreamId(streamId);
            for (ProjectTask task : tasks) {
                if (task.getProjectTaskId() == null) {
                    continue;
                }
                recalculateAfterTaskChange(task.getProjectTaskId());
            }
        }

        projectStreamDateRecalculationService.recalculateStreamDatesForProject(projectCode.trim());
    }

    private void validateDependencyChainsUpFront(Long rootTaskId) {
        Queue<Long> queue = new ArrayDeque<>();
        Set<Long> seen = new HashSet<>();
        queue.add(rootTaskId);

        while (!queue.isEmpty()) {
            Long taskId = queue.poll();
            if (taskId == null || !seen.add(taskId)) {
                continue;
            }

            Optional<ProjectTask> taskOptional = projectTaskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                continue;
            }

            ProjectTask task = taskOptional.get();
            projectTaskDependencyService.validateNoDependencyCycle(task);

            List<ProjectTask> children = projectTaskRepository.findByParentTaskId(taskId);
            for (ProjectTask child : children) {
                if (child.getProjectTaskId() != null) {
                    queue.add(child.getProjectTaskId());
                }
            }

            Long linkedMilestoneId = task.getMilestoneTaskId();
            if (linkedMilestoneId != null) {
                queue.add(linkedMilestoneId);
            }
        }
    }

    private void recalculateDependentBranch(Long parentTaskId, Set<Long> branchVisited, Set<Long> processedMilestones, Set<Long> affectedStreamIds) {
        if (parentTaskId == null || !branchVisited.add(parentTaskId)) {
            return;
        }

        List<ProjectTask> children = projectTaskRepository.findByParentTaskId(parentTaskId);
        for (ProjectTask child : children) {
            if (child.getProjectTaskId() == null) {
                continue;
            }

            ProjectTask recalculatedChild = projectTaskDateCalculationService.calculateTaskDates(child);
            projectTaskRepository.save(recalculatedChild);

            if (recalculatedChild.getProjectStreamId() != null) {
                affectedStreamIds.add(recalculatedChild.getProjectStreamId());
            }

            processLinkedMilestone(recalculatedChild, branchVisited, processedMilestones, affectedStreamIds);
            recalculateDependentBranch(recalculatedChild.getProjectTaskId(), branchVisited, processedMilestones, affectedStreamIds);
        }
    }

    private void processLinkedMilestone(ProjectTask task, Set<Long> branchVisited, Set<Long> processedMilestones, Set<Long> affectedStreamIds) {
        Long milestoneTaskId = task.getMilestoneTaskId();
        if (milestoneTaskId == null || !processedMilestones.add(milestoneTaskId)) {
            return;
        }

        recalculateMilestoneDate(milestoneTaskId, affectedStreamIds);
        recalculateDependentBranch(milestoneTaskId, branchVisited, processedMilestones, affectedStreamIds);
    }

    private void recalculateMilestoneDate(Long milestoneTaskId, Set<Long> affectedStreamIds) {
        Optional<ProjectTask> milestoneOptional = projectTaskRepository.findById(milestoneTaskId);
        if (milestoneOptional.isEmpty()) {
            return;
        }

        List<ProjectTask> milestoneParents = projectTaskRepository.findByMilestoneTaskId(milestoneTaskId);
        LocalDate furthestEndDate = null;
        for (ProjectTask parent : milestoneParents) {
            LocalDate parentEndDate = parseToLocalDate(resolveEffectiveEndDate(parent));
            if (parentEndDate == null) {
                continue;
            }
            if (furthestEndDate == null || parentEndDate.isAfter(furthestEndDate)) {
                furthestEndDate = parentEndDate;
            }
        }

        if (furthestEndDate == null) {
            return;
        }

        ProjectTask milestone = milestoneOptional.get();
        boolean canRecalculateStart = canRecalculateStart(milestone);
        boolean canRecalculateEnd = canRecalculateEnd(milestone);
        if (!canRecalculateStart && !canRecalculateEnd) {
            return;
        }

        String milestoneDate = furthestEndDate.toString();
        if (canRecalculateStart) {
            milestone.setTaskStartDate(milestoneDate);
        }
        if (canRecalculateEnd) {
            milestone.setTaskEndDate(milestoneDate);
        }
        if (milestone.getProjectStreamId() != null) {
            affectedStreamIds.add(milestone.getProjectStreamId());
        }
        projectTaskRepository.save(milestone);
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

    private String resolveEffectiveEndDate(ProjectTask task) {
        if (task == null) {
            return null;
        }

        String status = task.getTaskStatus() == null ? "" : task.getTaskStatus().trim().toLowerCase();
        if ("completed".equals(status)) {
            return task.getActualEndDate();
        }
        return task.getTaskEndDate();
    }

    private boolean canRecalculateStart(ProjectTask task) {
        if (task == null || task.getTaskStatus() == null) {
            return false;
        }
        return "not started".equals(task.getTaskStatus().trim().toLowerCase());
    }

    private boolean canRecalculateEnd(ProjectTask task) {
        if (task == null || task.getTaskStatus() == null) {
            return true;
        }
        return !"completed".equals(task.getTaskStatus().trim().toLowerCase());
    }
}
