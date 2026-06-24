package com.hcteol.jwt.backend.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.hcteol.jwt.backend.entities.ProjectInventoryView;

public class ProjectInventoryViewSpecification {

    public static Specification<ProjectInventoryView> withFilters(
            String projectCode,
            String inventoryType,
            Long productId,
            Long activityId,
            String status,
            Long minQuantity,
            Long maxQuantity) {

        return Specification
                .where(projectCode != null && !projectCode.isBlank() ? byProjectCode(projectCode) : null)
                .and(inventoryType != null && !inventoryType.isBlank() ? byInventoryType(inventoryType) : null)
                .and(productId != null ? byProductId(productId) : null)
                .and(activityId != null ? byActivityId(activityId) : null)
                .and(status != null && !status.isBlank() ? byStatus(status) : null)
                .and(minQuantity != null ? byMinQuantity(minQuantity) : null)
                .and(maxQuantity != null ? byMaxQuantity(maxQuantity) : null);
    }

    public static Specification<ProjectInventoryView> byProjectCode(String projectCode) {
        return (root, query, cb) -> cb.equal(root.get("projectCode"), projectCode);
    }

    public static Specification<ProjectInventoryView> byInventoryType(String inventoryType) {
        return (root, query, cb) -> cb.equal(root.get("inventoryType"), inventoryType);
    }

    public static Specification<ProjectInventoryView> byProductId(Long productId) {
        return (root, query, cb) -> cb.equal(root.get("productId"), productId);
    }

    public static Specification<ProjectInventoryView> byActivityId(Long activityId) {
        return (root, query, cb) -> cb.equal(root.get("activityId"), activityId);
    }

    public static Specification<ProjectInventoryView> byStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ProjectInventoryView> byMinQuantity(Long minQuantity) {
        return (root, query, cb) -> cb.ge(root.get("quantity"), minQuantity);
    }

    public static Specification<ProjectInventoryView> byMaxQuantity(Long maxQuantity) {
        return (root, query, cb) -> cb.le(root.get("quantity"), maxQuantity);
    }
}
