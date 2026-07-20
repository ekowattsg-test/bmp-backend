package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectStreamAsset;
import com.hcteol.jwt.backend.repositories.ProjectStreamAssetRepository;

@Service
public class ProjectStreamAssetService {

    @Autowired
    private ProjectStreamAssetRepository projectStreamAssetRepository;

    public List<ProjectStreamAsset> getAllProjectStreamAssets() {
        return projectStreamAssetRepository.findAll();
    }

    public List<ProjectStreamAsset> getProjectStreamAssets(Long projectStreamId, Long requisitionCycleId) {
        if (projectStreamId != null && requisitionCycleId != null) {
            return projectStreamAssetRepository.findByProjectStreamIdAndRequisitionCycleId(projectStreamId,
                    requisitionCycleId);
        }
        if (projectStreamId != null) {
            return projectStreamAssetRepository.findByProjectStreamId(projectStreamId);
        }
        if (requisitionCycleId != null) {
            return projectStreamAssetRepository.findByRequisitionCycleId(requisitionCycleId);
        }
        return projectStreamAssetRepository.findAll();
    }

    public Optional<ProjectStreamAsset> getProjectStreamAssetById(Long id) {
        return projectStreamAssetRepository.findById(id);
    }

    public List<ProjectStreamAsset> getProjectStreamAssetsByStreamId(Long projectStreamId) {
        return projectStreamAssetRepository.findByProjectStreamId(projectStreamId);
    }

    public List<ProjectStreamAsset> getProjectStreamAssetsByRequisitionCycleId(Long requisitionCycleId) {
        return projectStreamAssetRepository.findByRequisitionCycleId(requisitionCycleId);
    }

    public ProjectStreamAsset createProjectStreamAsset(ProjectStreamAsset projectStreamAsset) {
        return projectStreamAssetRepository.save(projectStreamAsset);
    }

    public ProjectStreamAsset updateProjectStreamAsset(Long id, ProjectStreamAsset projectStreamAssetDetails) {
        return projectStreamAssetRepository.findById(id).map(projectStreamAsset -> {
            projectStreamAsset.setProjectStreamId(projectStreamAssetDetails.getProjectStreamId());
            projectStreamAsset.setProductId(projectStreamAssetDetails.getProductId());
            projectStreamAsset.setQuantity(projectStreamAssetDetails.getQuantity());
            projectStreamAsset.setRequisitionCycleId(projectStreamAssetDetails.getRequisitionCycleId());
            return projectStreamAssetRepository.save(projectStreamAsset);
        }).orElseThrow(() -> new RuntimeException("ProjectStreamAsset not found with id " + id));
    }

    public void deleteProjectStreamAsset(Long id) {
        projectStreamAssetRepository.deleteById(id);
    }
}
