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
        return savedTask;
    }

    public ProjectTask calculateProjectTask(ProjectTask inputTask) {
        return projectTaskDateCalculationService.calculateTaskDates(inputTask);
    }

    @Transactional
    public ProjectTask updateProjectTask(Long id, ProjectTask projectTaskDetails) {
        return projectTaskRepository.findById(id).map(projectTask -> {
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
            projectTask.setActualStartDate(projectTaskDetails.getActualStartDate());
            projectTask.setActualEndDate(projectTaskDetails.getActualEndDate());
            projectTask.setRemarks(projectTaskDetails.getRemarks());
            ProjectTask savedTask = projectTaskRepository.save(projectTask);
            projectTaskRepository.flush();
            projectTaskRecalculationService.recalculateAfterTaskChange(savedTask.getProjectTaskId());
            return savedTask;
        }).orElseThrow(() -> new RuntimeException("ProjectTask not found with id " + id));
    }

    public void deleteProjectTask(Long id) {
        projectTaskRepository.deleteById(id);
    }

}
