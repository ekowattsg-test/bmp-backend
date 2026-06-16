package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectTaskType;
import com.hcteol.jwt.backend.repositories.ProjectTaskTypeRepository;

@Service
public class ProjectTaskTypeService {

    private final ProjectTaskTypeRepository projectTaskTypeRepository;

    @Autowired
    public ProjectTaskTypeService(ProjectTaskTypeRepository projectTaskTypeRepository) {
        this.projectTaskTypeRepository = projectTaskTypeRepository;
    }

    public List<ProjectTaskType> getAllProjectTaskTypes() {
        return projectTaskTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "projectTaskCode"));
    }

    public Optional<ProjectTaskType> getProjectTaskTypeByCode(String projectTaskCode) {
        return projectTaskTypeRepository.findById(Objects.requireNonNull(projectTaskCode, "projectTaskCode must not be null"));
    }

    public ProjectTaskType createProjectTaskType(ProjectTaskType projectTaskType) {
        return projectTaskTypeRepository.save(Objects.requireNonNull(projectTaskType, "projectTaskType must not be null"));
    }

    public Optional<ProjectTaskType> updateProjectTaskType(String projectTaskCode, ProjectTaskType projectTaskTypeDetails) {
        Objects.requireNonNull(projectTaskTypeDetails, "projectTaskTypeDetails must not be null");
        return projectTaskTypeRepository.findById(Objects.requireNonNull(projectTaskCode, "projectTaskCode must not be null")).map(projectTaskType -> {
            projectTaskType.setProjectTaskDescription(projectTaskTypeDetails.getProjectTaskDescription());
            projectTaskType.setUserTask(projectTaskTypeDetails.getUserTask());
            projectTaskType.setEditStartDate(projectTaskTypeDetails.getEditStartDate());
            projectTaskType.setCreateByStream(projectTaskTypeDetails.getCreateByStream());
            projectTaskType.setCanDelete(projectTaskTypeDetails.getCanDelete());
            projectTaskType.setMinimumDays(projectTaskTypeDetails.getMinimumDays());
            projectTaskType.setMaximumDays(projectTaskTypeDetails.getMaximumDays());
            projectTaskType.setAlignWith(projectTaskTypeDetails.getAlignWith());
            projectTaskType.setInventoryType(projectTaskTypeDetails.getInventoryType());
            projectTaskType.setManpowerRequired(projectTaskTypeDetails.getManpowerRequired());
            return projectTaskTypeRepository.save(projectTaskType);
        });
    }

    public boolean deleteProjectTaskType(String projectTaskCode) {
        String code = Objects.requireNonNull(projectTaskCode, "projectTaskCode must not be null");
        if (!projectTaskTypeRepository.existsById(code)) {
            return false;
        }
        projectTaskTypeRepository.deleteById(code);
        return true;
    }
}
