package com.hcteol.jwt.backend.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskDependencyService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public void validateNoDependencyCycle(ProjectTask inputTask) {
        if (inputTask == null) {
            throw new IllegalArgumentException("projectTask is required");
        }

        Long parentId = inputTask.getParentTaskId();
        if (parentId == null) {
            return;
        }

        Set<Long> visited = new HashSet<>();
        Long currentTaskId = inputTask.getProjectTaskId();
        if (currentTaskId != null) {
            visited.add(currentTaskId);
        }

        Long cursor = parentId;
        while (cursor != null) {
            if (!visited.add(cursor)) {
                throw new IllegalArgumentException("Task dependency causes an infinite loop");
            }

            Optional<ProjectTask> parentTaskOptional = projectTaskRepository.findById(cursor);
            if (parentTaskOptional.isEmpty()) {
                return;
            }

            cursor = parentTaskOptional.get().getParentTaskId();
        }
    }
}
