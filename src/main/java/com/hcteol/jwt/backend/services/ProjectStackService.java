package com.hcteol.jwt.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectStack;
import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.entities.ProjectUnitWork;
import com.hcteol.jwt.backend.repositories.ProjectBlockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStackRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitWorkRepository;

@Service
public class ProjectStackService {

    @Autowired
    private ProjectStackRepository projectStackRepository;

    @Autowired
    private ProjectBlockRepository projectBlockRepository;

    @Autowired
    private ProjectUnitRepository projectUnitRepository;

    @Autowired
    private ProjectUnitWorkRepository projectUnitWorkRepository;

    public List<ProjectStack> getProjectStacksByBlockId(Long projectBlockId) {
        return projectStackRepository.findByProjectBlockIdOrderByStackNumberAsc(projectBlockId);
    }

    public Optional<ProjectStack> getProjectStackById(Long id) {
        return projectStackRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public ProjectStack createProjectStack(ProjectStack projectStack) {
        validateProjectBlock(projectStack.getProjectBlockId());
        validateUniqueStackNumber(projectStack.getProjectBlockId(), projectStack.getStackNumber(), null);
        return projectStackRepository.save(projectStack);
    }

    @Transactional
    public ProjectStack updateProjectStack(Long id, ProjectStack projectStackDetails) {
        return projectStackRepository.findById(Objects.requireNonNull(id, "id cannot be null")).map(projectStack -> {
            validateProjectBlock(projectStackDetails.getProjectBlockId());
            projectStack.setProjectBlockId(projectStackDetails.getProjectBlockId());
            projectStack.setStackName(projectStackDetails.getStackName());
            projectStack.setStackDescription(projectStackDetails.getStackDescription());
            projectStack.setStackNumber(projectStackDetails.getStackNumber());
            projectStack.setStatus(projectStackDetails.getStatus());
            validateUniqueStackNumber(projectStack.getProjectBlockId(), projectStack.getStackNumber(), projectStack.getProjectStackId());
            return projectStackRepository.save(projectStack);
        }).orElseThrow(() -> new RuntimeException("ProjectStack not found with id " + id));
    }

    @Transactional
    public void deleteProjectStack(Long id) {
        ProjectStack stack = projectStackRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElseThrow(() -> new RuntimeException("ProjectStack not found with id " + id));

        List<ProjectUnit> units = projectUnitRepository.findByProjectStackId(stack.getProjectStackId());
        for (ProjectUnit unit : units) {
            List<ProjectUnitWork> works = projectUnitWorkRepository.findByProjectUnitId(unit.getProjectUnitId());
            projectUnitWorkRepository.deleteAll(new ArrayList<>(works));
        }
        projectUnitRepository.deleteAll(new ArrayList<>(units));
        projectStackRepository.delete(stack);
    }

    private void validateProjectBlock(Long projectBlockId) {
        if (projectBlockId == null || projectBlockRepository.findById(Objects.requireNonNull(projectBlockId, "projectBlockId cannot be null")).isEmpty()) {
            throw new IllegalArgumentException("projectBlockId is required and must exist");
        }
    }

    private void validateUniqueStackNumber(Long projectBlockId, Long stackNumber, Long currentId) {
        if (projectBlockId == null || stackNumber == null) {
            return;
        }

        for (ProjectStack existing : projectStackRepository.findByProjectBlockIdAndStackNumber(projectBlockId, stackNumber)) {
            if (currentId == null || !currentId.equals(existing.getProjectStackId())) {
                throw new IllegalArgumentException("Stack number must be unique within a block");
            }
        }
    }
}
