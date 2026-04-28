package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.ProjectBundle;
import com.hcteol.jwt.backend.repositories.ProjectBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectBundleService {
    @Autowired
    private ProjectBundleRepository projectBundleRepository;

    public List<ProjectBundle> getAllProjectBundles() {
        return projectBundleRepository.findAll();
    }

    public Optional<ProjectBundle> getProjectBundleById(Long id) {
        return projectBundleRepository.findById(id);
    }

    public List<ProjectBundle> getProjectBundlesByTaskId(Long projectTaskId) {
        return projectBundleRepository.findByProjectTaskId(projectTaskId);
    }

    public ProjectBundle createProjectBundle(ProjectBundle projectBundle) {
        return projectBundleRepository.save(projectBundle);
    }

    public ProjectBundle updateProjectBundle(Long id, ProjectBundle projectBundleDetails) {
        return projectBundleRepository.findById(id).map(projectBundle -> {
            projectBundle.setProjectTaskId(projectBundleDetails.getProjectTaskId());
            projectBundle.setBundleId(projectBundleDetails.getBundleId());
            projectBundle.setQuantity(projectBundleDetails.getQuantity());
            return projectBundleRepository.save(projectBundle);
        }).orElseThrow(() -> new RuntimeException("ProjectBundle not found with id " + id));
    }

    public void deleteProjectBundle(Long id) {
        projectBundleRepository.deleteById(id);
    }
}
