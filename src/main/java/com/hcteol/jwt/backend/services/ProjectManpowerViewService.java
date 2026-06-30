package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectManpowerView;
import com.hcteol.jwt.backend.repositories.ProjectManpowerViewRepository;
import com.hcteol.jwt.backend.specifications.ProjectManpowerViewSpecification;

@Service
public class ProjectManpowerViewService {

    @Autowired
    private ProjectManpowerViewRepository projectManpowerViewRepository;

    public List<ProjectManpowerView> getProjectManpowerViews(
            String projectCode,
            Long projectStreamId,
            Long projectTaskId,
            String staffId,
            String taskStatus,
            Integer active,
            String projectStatus) {

        Specification<ProjectManpowerView> spec = ProjectManpowerViewSpecification.withFilters(
                projectCode,
                projectStreamId,
                projectTaskId,
                staffId,
                taskStatus,
                active,
                projectStatus);

        return projectManpowerViewRepository.findAll(spec);
    }

    public Optional<ProjectManpowerView> getProjectManpowerViewById(Long rowId) {
        if (rowId == null) {
            return Optional.empty();
        }
        return projectManpowerViewRepository.findById(rowId);
    }

    public List<ProjectManpowerView> getProjectManpowerViewsByProjectCode(String projectCode) {
        return projectManpowerViewRepository.findByProjectCode(projectCode);
    }

    public List<ProjectManpowerView> getProjectManpowerViewsByTaskId(Long projectTaskId) {
        return projectManpowerViewRepository.findByProjectTaskId(projectTaskId);
    }

    public List<ProjectManpowerView> getProjectManpowerViewsByStaffId(String staffId) {
        return projectManpowerViewRepository.findByStaffId(staffId);
    }
}
