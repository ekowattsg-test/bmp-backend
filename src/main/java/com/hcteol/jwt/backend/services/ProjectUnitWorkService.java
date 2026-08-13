package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.entities.ProjectUnitWork;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitWorkRepository;

@Service
public class ProjectUnitWorkService {

    @Autowired
    private ProjectUnitWorkRepository projectUnitWorkRepository;

    @Autowired
    private ProjectUnitRepository projectUnitRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public List<ProjectUnitWork> getProjectUnitWorksByUnitId(Long projectUnitId) {
        return projectUnitWorkRepository.findByProjectUnitIdOrderByProjectUnitWorkIdAsc(projectUnitId);
    }

    public Optional<ProjectUnitWork> getProjectUnitWorkById(Long id) {
        return projectUnitWorkRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public ProjectUnitWork createProjectUnitWork(ProjectUnitWork projectUnitWork) {
        ProjectUnit unit = validateProjectUnit(projectUnitWork.getProjectUnitId());
        ProjectTask task = validateProjectTask(projectUnitWork.getProjectTaskId(), unit.getProjectStreamId());
        validateUniqueTaskMapping(projectUnitWork.getProjectUnitId(), projectUnitWork.getProjectTaskId(), null);
        if (task != null) {
            projectUnitWork.setWorkName(task.getTaskName());
        }
        return projectUnitWorkRepository.save(projectUnitWork);
    }

    @Transactional
    public void deleteProjectUnitWork(Long id) {
        projectUnitWorkRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }

    private ProjectUnit validateProjectUnit(Long projectUnitId) {
        if (projectUnitId == null) {
            throw new IllegalArgumentException("projectUnitId is required");
        }

        return projectUnitRepository.findById(projectUnitId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectUnit not found with id " + projectUnitId));
    }

    private ProjectTask validateProjectTask(Long projectTaskId, Long projectStreamId) {
        if (projectTaskId == null) {
            throw new IllegalArgumentException("projectTaskId is required");
        }

        ProjectTask task = projectTaskRepository.findById(projectTaskId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectTask not found with id " + projectTaskId));

        if (projectStreamId == null) {
            throw new IllegalArgumentException("projectStreamId must be set on the unit before mapping works");
        }

        if (task.getProjectStreamId() != null && !projectStreamId.equals(task.getProjectStreamId())) {
            throw new IllegalArgumentException("projectTaskId must belong to the same project stream as the unit");
        }

        return task;
    }

    private void validateUniqueTaskMapping(Long projectUnitId, Long projectTaskId, Long currentId) {
        if (projectUnitId == null || projectTaskId == null) {
            return;
        }

        for (ProjectUnitWork existing : projectUnitWorkRepository.findByProjectUnitIdAndProjectTaskId(projectUnitId, projectTaskId)) {
            if (currentId == null || !currentId.equals(existing.getProjectUnitWorkId())) {
                throw new IllegalArgumentException("Work can map to a task only once per unit");
            }
        }
    }
}
