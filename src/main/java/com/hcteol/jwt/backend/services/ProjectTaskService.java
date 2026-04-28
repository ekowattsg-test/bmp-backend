package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectTaskService {
    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public List<ProjectTask> getAllProjectTasks() {
        return projectTaskRepository.findAll();
    }

    public Optional<ProjectTask> getProjectTaskById(Long id) {
        return projectTaskRepository.findById(id);
    }

    public List<ProjectTask> getProjectTasksByStreamId(Long projectStreamId) {
        return projectTaskRepository.findByProjectStreamId(projectStreamId);
    }

    public ProjectTask createProjectTask(ProjectTask projectTask) {
        return projectTaskRepository.save(projectTask);
    }

    public ProjectTask updateProjectTask(Long id, ProjectTask projectTaskDetails) {
        return projectTaskRepository.findById(id).map(projectTask -> {
            projectTask.setProjectStreamId(projectTaskDetails.getProjectStreamId());
            projectTask.setTaskType(projectTaskDetails.getTaskType());
            projectTask.setTaskName(projectTaskDetails.getTaskName());
            projectTask.setMobileNumber(projectTaskDetails.getMobileNumber());
            projectTask.setTaskStartDate(projectTaskDetails.getTaskStartDate());
            projectTask.setTaskEndDate(projectTaskDetails.getTaskEndDate());
            return projectTaskRepository.save(projectTask);
        }).orElseThrow(() -> new RuntimeException("ProjectTask not found with id " + id));
    }

    public void deleteProjectTask(Long id) {
        projectTaskRepository.deleteById(id);
    }
}
