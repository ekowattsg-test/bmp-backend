package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectAsset;
import com.hcteol.jwt.backend.repositories.ProjectAssetRepository;

@Service
public class ProjectAssetService {

    @Autowired
    private ProjectAssetRepository projectAssetRepository;

    public List<ProjectAsset> getAllProjectAssets() {
        return projectAssetRepository.findAll();
    }

    public Optional<ProjectAsset> getProjectAssetById(Long id) {
        return projectAssetRepository.findById(id);
    }

    public List<ProjectAsset> getProjectAssetsByTaskId(Long projectTaskId) {
        return projectAssetRepository.findByProjectTaskId(projectTaskId);
    }

    public ProjectAsset createProjectAsset(ProjectAsset projectAsset) {
        return projectAssetRepository.save(projectAsset);
    }

    public ProjectAsset updateProjectAsset(Long id, ProjectAsset projectAssetDetails) {
        return projectAssetRepository.findById(id).map(projectAsset -> {
            projectAsset.setProjectTaskId(projectAssetDetails.getProjectTaskId());
            projectAsset.setProductId(projectAssetDetails.getProductId());
            projectAsset.setQuantity(projectAssetDetails.getQuantity());
            return projectAssetRepository.save(projectAsset);
        }).orElseThrow(() -> new RuntimeException("ProjectAsset not found with id " + id));
    }

    public void deleteProjectAsset(Long id) {
        projectAssetRepository.deleteById(id);
    }
}
