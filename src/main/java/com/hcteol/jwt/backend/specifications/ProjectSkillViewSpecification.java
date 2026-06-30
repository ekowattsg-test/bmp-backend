package com.hcteol.jwt.backend.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.hcteol.jwt.backend.entities.ProjectSkillView;

public final class ProjectSkillViewSpecification {

    private ProjectSkillViewSpecification() {
    }

    public static Specification<ProjectSkillView> withFilters(
            String projectCode,
            Long projectStreamId,
            Long projectTaskId,
            Long staffSkillId,
            String taskStatus,
            Integer active,
            String projectStatus) {

        Specification<ProjectSkillView> spec = Specification.where(null);

        if (projectCode != null && !projectCode.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("projectCode"), projectCode));
        }
        if (projectStreamId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("projectStreamId"), projectStreamId));
        }
        if (projectTaskId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("projectTaskId"), projectTaskId));
        }
        if (staffSkillId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("staffSkillId"), staffSkillId));
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
