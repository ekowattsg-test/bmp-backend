package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectSkillView;
import com.hcteol.jwt.backend.repositories.ProjectSkillViewRepository;
import com.hcteol.jwt.backend.specifications.ProjectSkillViewSpecification;

@Service
public class ProjectSkillViewService {

    @Autowired
    private ProjectSkillViewRepository projectSkillViewRepository;

    public List<ProjectSkillView> getProjectSkillViews(
            String projectCode,
            Long projectStreamId,
            Long projectTaskId,
            Long staffSkillId,
            String taskStatus,
            Integer active,
            String projectStatus) {

        Specification<ProjectSkillView> spec = ProjectSkillViewSpecification.withFilters(
                projectCode,
                projectStreamId,
                projectTaskId,
                staffSkillId,
                taskStatus,
                active,
                projectStatus);

        return projectSkillViewRepository.findAll(spec);
    }

    public Optional<ProjectSkillView> getProjectSkillViewById(Long rowId) {
        if (rowId == null) {
            return Optional.empty();
        }
        return projectSkillViewRepository.findById(rowId);
    }

    public List<ProjectSkillView> getProjectSkillViewsByProjectCode(String projectCode) {
        return projectSkillViewRepository.findByProjectCode(projectCode);
    }

    public List<ProjectSkillView> getProjectSkillViewsByTaskId(Long projectTaskId) {
        return projectSkillViewRepository.findByProjectTaskId(projectTaskId);
    }

    public List<ProjectSkillView> getProjectSkillViewsByStaffSkillId(Long staffSkillId) {
        return projectSkillViewRepository.findByStaffSkillId(staffSkillId);
    }
}
