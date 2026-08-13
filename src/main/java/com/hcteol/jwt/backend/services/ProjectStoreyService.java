package com.hcteol.jwt.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectStorey;
import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.entities.ProjectUnitWork;
import com.hcteol.jwt.backend.repositories.ProjectBlockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStoreyRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitWorkRepository;

@Service
public class ProjectStoreyService {

    @Autowired
    private ProjectStoreyRepository projectStoreyRepository;

    @Autowired
    private ProjectBlockRepository projectBlockRepository;

    @Autowired
    private ProjectUnitRepository projectUnitRepository;

    @Autowired
    private ProjectUnitWorkRepository projectUnitWorkRepository;

    public List<ProjectStorey> getProjectStoreysByBlockId(Long projectBlockId) {
        return projectStoreyRepository.findByProjectBlockIdOrderByStoreyNumberAsc(projectBlockId);
    }

    public Optional<ProjectStorey> getProjectStoreyById(Long id) {
        return projectStoreyRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public ProjectStorey createProjectStorey(ProjectStorey projectStorey) {
        validateProjectBlock(projectStorey.getProjectBlockId());
        validateUniqueStoreyNumber(projectStorey.getProjectBlockId(), projectStorey.getStoreyNumber(), null);
        return projectStoreyRepository.save(projectStorey);
    }

    @Transactional
    public ProjectStorey updateProjectStorey(Long id, ProjectStorey projectStoreyDetails) {
        return projectStoreyRepository.findById(Objects.requireNonNull(id, "id cannot be null")).map(projectStorey -> {
            validateProjectBlock(projectStoreyDetails.getProjectBlockId());
            projectStorey.setProjectBlockId(projectStoreyDetails.getProjectBlockId());
            projectStorey.setStoreyName(projectStoreyDetails.getStoreyName());
            projectStorey.setStoreyDescription(projectStoreyDetails.getStoreyDescription());
            projectStorey.setStoreyNumber(projectStoreyDetails.getStoreyNumber());
            projectStorey.setStatus(projectStoreyDetails.getStatus());
            validateUniqueStoreyNumber(projectStorey.getProjectBlockId(), projectStorey.getStoreyNumber(), projectStorey.getProjectStoreyId());
            return projectStoreyRepository.save(projectStorey);
        }).orElseThrow(() -> new RuntimeException("ProjectStorey not found with id " + id));
    }

    @Transactional
    public void deleteProjectStorey(Long id) {
        ProjectStorey storey = projectStoreyRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElseThrow(() -> new RuntimeException("ProjectStorey not found with id " + id));

        List<ProjectUnit> units = projectUnitRepository.findByProjectStoreyId(storey.getProjectStoreyId());
        for (ProjectUnit unit : units) {
            List<ProjectUnitWork> works = projectUnitWorkRepository.findByProjectUnitId(unit.getProjectUnitId());
            projectUnitWorkRepository.deleteAll(new ArrayList<>(works));
        }
        projectUnitRepository.deleteAll(new ArrayList<>(units));
        projectStoreyRepository.delete(storey);
    }

    private void validateProjectBlock(Long projectBlockId) {
        if (projectBlockId == null || projectBlockRepository.findById(Objects.requireNonNull(projectBlockId, "projectBlockId cannot be null")).isEmpty()) {
            throw new IllegalArgumentException("projectBlockId is required and must exist");
        }
    }

    private void validateUniqueStoreyNumber(Long projectBlockId, Long storeyNumber, Long currentId) {
        if (projectBlockId == null || storeyNumber == null) {
            return;
        }

        for (ProjectStorey existing : projectStoreyRepository.findByProjectBlockIdAndStoreyNumber(projectBlockId, storeyNumber)) {
            if (currentId == null || !currentId.equals(existing.getProjectStoreyId())) {
                throw new IllegalArgumentException("Storey number must be unique within a block");
            }
        }
    }
}
