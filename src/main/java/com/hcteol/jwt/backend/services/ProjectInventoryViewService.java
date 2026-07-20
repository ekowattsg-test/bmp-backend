package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectInventoryView;
import com.hcteol.jwt.backend.repositories.ProjectInventoryViewRepository;
import com.hcteol.jwt.backend.specifications.ProjectInventoryViewSpecification;

@Service
public class ProjectInventoryViewService {

    @Autowired
    private ProjectInventoryViewRepository projectInventoryViewRepository;

    /**
     * Multi-dimensional search with optional filters for project code,
     * inventory type, product ID, activity ID, status, and quantity range. All
     * parameters are optional; only non-null parameters are applied as filters.
     */
    public List<ProjectInventoryView> getProjectInventoryViews(
            String projectCode,
            String inventoryType,
            Long productId,
            Long activityId,
            Long requisitionCycleId,
            String status,
            Long minQuantity,
            Long maxQuantity) {

        Specification<ProjectInventoryView> spec = ProjectInventoryViewSpecification.withFilters(
                projectCode, inventoryType, productId, activityId, requisitionCycleId, status, minQuantity,
                maxQuantity);

        return projectInventoryViewRepository.findAll(spec);
    }

    /**
     * Legacy overload for backward compatibility (4 parameters).
     */
    public List<ProjectInventoryView> getProjectInventoryViews(
            String projectCode,
            String inventoryType,
            Long productId,
            Long activityId) {
        return getProjectInventoryViews(projectCode, inventoryType, productId, activityId, null, null, null, null);
    }

    public Optional<ProjectInventoryView> getProjectInventoryViewById(String rowId) {
        return projectInventoryViewRepository.findById(rowId);
    }

    public List<ProjectInventoryView> getProjectInventoryViewsByProjectCode(String projectCode) {
        return projectInventoryViewRepository.findByProjectCode(projectCode);
    }

    public List<ProjectInventoryView> getProjectInventoryViewsByProductId(Long productId) {
        return projectInventoryViewRepository.findByProductId(productId);
    }

    public List<ProjectInventoryView> getProjectInventoryViewsByActivityId(Long activityId) {
        return projectInventoryViewRepository.findByActivityId(activityId);
    }

    public List<ProjectInventoryView> getProjectInventoryViewsByRequisitionCycleId(Long requisitionCycleId) {
        return projectInventoryViewRepository.findByRequisitionCycleId(requisitionCycleId);
    }
}
