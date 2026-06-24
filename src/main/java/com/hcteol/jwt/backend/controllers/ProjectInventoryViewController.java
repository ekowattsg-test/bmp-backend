package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.ProjectInventoryView;
import com.hcteol.jwt.backend.services.ProjectInventoryViewService;

@RestController
@RequestMapping("/api/projectinventoryviews")
public class ProjectInventoryViewController {

    @Autowired
    private ProjectInventoryViewService projectInventoryViewService;

    /**
     * Multi-dimensional search endpoint with optional filters. Supports
     * filtering by: - projectCode: Filter by project - inventoryType: Filter by
     * inventory type (Asset, Stock, Bundle, StreamAsset, StreamBundle) -
     * productId: Filter by product - activityId: Filter by activity (task or
     * stream) - status: Filter by status (Not Started, In Progress, Completed)
     * - minQuantity: Filter by minimum quantity - maxQuantity: Filter by
     * maximum quantity
     *
     * Example:
     * /api/projectinventoryviews?projectCode=P001&inventoryType=Asset&minQuantity=10
     */
    @GetMapping
    public List<ProjectInventoryView> searchProjectInventoryViews(
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) String inventoryType,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long minQuantity,
            @RequestParam(required = false) Long maxQuantity) {
        return projectInventoryViewService.getProjectInventoryViews(
                projectCode, inventoryType, productId, activityId, status, minQuantity, maxQuantity);
    }

    /**
     * Get a single inventory view record by row ID.
     *
     * Example: /api/projectinventoryviews/Asset-123-456
     */
    @GetMapping("/{rowId}")
    public ResponseEntity<ProjectInventoryView> getProjectInventoryViewById(@PathVariable String rowId) {
        return projectInventoryViewService.getProjectInventoryViewById(rowId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all inventory for a specific project.
     *
     * Example: /api/projectinventoryviews/project/P001
     */
    @GetMapping("/project/{projectCode}")
    public List<ProjectInventoryView> getByProjectCode(@PathVariable String projectCode) {
        return projectInventoryViewService.getProjectInventoryViewsByProjectCode(projectCode);
    }

    /**
     * Get all inventory for a specific product.
     *
     * Example: /api/projectinventoryviews/product/42
     */
    @GetMapping("/product/{productId}")
    public List<ProjectInventoryView> getByProductId(@PathVariable Long productId) {
        return projectInventoryViewService.getProjectInventoryViewsByProductId(productId);
    }

    /**
     * Get all inventory for a specific activity (task or stream).
     *
     * Example: /api/projectinventoryviews/activity/99
     */
    @GetMapping("/activity/{activityId}")
    public List<ProjectInventoryView> getByActivityId(@PathVariable Long activityId) {
        return projectInventoryViewService.getProjectInventoryViewsByActivityId(activityId);
    }
}
