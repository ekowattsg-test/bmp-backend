package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

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

            return refreshedTask;
        }).orElseThrow(() -> new RuntimeException("ProjectTask not found with id " + id));
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
