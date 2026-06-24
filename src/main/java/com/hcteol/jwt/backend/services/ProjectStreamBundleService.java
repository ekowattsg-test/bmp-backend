package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectStreamBundle;
import com.hcteol.jwt.backend.repositories.ProjectStreamBundleRepository;

@Service
public class ProjectStreamBundleService {

    @Autowired
    private ProjectStreamBundleRepository projectStreamBundleRepository;

    public List<ProjectStreamBundle> getAllProjectStreamBundles() {
        return projectStreamBundleRepository.findAll();
    }

    public Optional<ProjectStreamBundle> getProjectStreamBundleById(Long id) {
        return projectStreamBundleRepository.findById(id);
    }

    public List<ProjectStreamBundle> getProjectStreamBundlesByStreamId(Long projectStreamId) {
        return projectStreamBundleRepository.findByProjectStreamId(projectStreamId);
    }

    public ProjectStreamBundle createProjectStreamBundle(ProjectStreamBundle projectStreamBundle) {
        return projectStreamBundleRepository.save(projectStreamBundle);
    }

    public ProjectStreamBundle updateProjectStreamBundle(Long id, ProjectStreamBundle projectStreamBundleDetails) {
        return projectStreamBundleRepository.findById(id).map(projectStreamBundle -> {
            projectStreamBundle.setProjectStreamId(projectStreamBundleDetails.getProjectStreamId());
            projectStreamBundle.setBundleId(projectStreamBundleDetails.getBundleId());
            projectStreamBundle.setQuantity(projectStreamBundleDetails.getQuantity());
            return projectStreamBundleRepository.save(projectStreamBundle);
        }).orElseThrow(() -> new RuntimeException("ProjectStreamBundle not found with id " + id));
    }

    public void deleteProjectStreamBundle(Long id) {
        projectStreamBundleRepository.deleteById(id);
    }
}
