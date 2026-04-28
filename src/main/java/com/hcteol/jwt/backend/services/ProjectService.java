package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectByCode(String projectCode) {
        return projectRepository.findById(projectCode);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(String projectCode, Project projectDetails) {
        return projectRepository.findById(projectCode).map(project -> {
            project.setProjectName(projectDetails.getProjectName());
            project.setProjectDescription(projectDetails.getProjectDescription());
            project.setCustomerId(projectDetails.getCustomerId());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setProjectLocation(projectDetails.getProjectLocation());
            project.setMobileNumber(projectDetails.getMobileNumber());
            project.setActive(projectDetails.getActive());
            project.setStreamCount(projectDetails.getStreamCount());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found with projectCode " + projectCode));
    }

    public void deleteProject(String projectCode) {
        projectRepository.deleteById(projectCode);
    }
}
