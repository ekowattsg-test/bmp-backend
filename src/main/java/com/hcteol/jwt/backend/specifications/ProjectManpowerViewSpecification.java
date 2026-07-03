package com.hcteol.jwt.backend.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.hcteol.jwt.backend.entities.ProjectManpowerView;

public final class ProjectManpowerViewSpecification {

    private ProjectManpowerViewSpecification() {
    }

    public static Specification<ProjectManpowerView> withFilters(
            String projectCode,
            Long projectStreamId,
            Long projectTaskId,
            Integer manpowerTouched,
            String workDate,
            String staffId,
            String taskStatus,
            Integer active,
            String projectStatus) {

        Specification<ProjectManpowerView> spec = Specification.where(null);

        if (projectCode != null && !projectCode.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("projectCode"), projectCode));
        }
        if (projectStreamId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("projectStreamId"), projectStreamId));
        }
        if (projectTaskId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("projectTaskId"), projectTaskId));
        }
        if (manpowerTouched != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("manpowerTouched"), manpowerTouched));
        }
        if (workDate != null && !workDate.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("workDate"), workDate));
        }
        if (staffId != null && !staffId.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("staffId"), staffId));
        }
        if (taskStatus != null && !taskStatus.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("taskStatus"), taskStatus));
        }
        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }
        if (projectStatus != null && !projectStatus.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), projectStatus));
        }

        return spec;
    }
}
