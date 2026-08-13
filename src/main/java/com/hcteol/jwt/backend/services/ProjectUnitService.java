package com.hcteol.jwt.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.ProjectBlock;
import com.hcteol.jwt.backend.entities.ProjectStack;
import com.hcteol.jwt.backend.entities.ProjectStorey;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.entities.ProjectUnitWork;
import com.hcteol.jwt.backend.repositories.ProjectBlockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStackRepository;
import com.hcteol.jwt.backend.repositories.ProjectStoreyRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitWorkRepository;

@Service
public class ProjectUnitService {

    @Autowired
    private ProjectUnitRepository projectUnitRepository;

    @Autowired
    private ProjectStoreyRepository projectStoreyRepository;

    @Autowired
    private ProjectStackRepository projectStackRepository;

    @Autowired
    private ProjectBlockRepository projectBlockRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectUnitWorkRepository projectUnitWorkRepository;

    public List<ProjectUnit> getProjectUnitsByStoreyId(Long projectStoreyId) {
        return projectUnitRepository.findByProjectStoreyIdOrderByUnitNumberAsc(projectStoreyId);
    }

    public List<ProjectUnit> getProjectUnitsByProjectCode(String projectCode) {
        List<ProjectUnit> units = new ArrayList<>();
        if (projectCode == null || projectCode.isBlank()) {
            return units;
        }

        List<ProjectBlock> blocks = projectBlockRepository.findByProjectCodeOrderByBlockNumberAsc(projectCode);
        for (ProjectBlock block : blocks) {
            List<ProjectStorey> storeys = projectStoreyRepository
                    .findByProjectBlockIdOrderByStoreyNumberAsc(block.getProjectBlockId());
            for (ProjectStorey storey : storeys) {
                units.addAll(projectUnitRepository.findByProjectStoreyIdOrderByUnitNumberAsc(storey.getProjectStoreyId()));
            }
        }

        return units;
    }

    public List<ProjectUnit> getProjectUnitsByStackId(Long projectStackId) {
        return projectUnitRepository.findByProjectStackIdOrderByUnitNumberAsc(projectStackId);
    }

    public List<ProjectUnit> getProjectUnitsByIntersection(Long projectStoreyId, Long projectStackId) {
        return projectUnitRepository.findByProjectStoreyIdAndProjectStackIdOrderByUnitNumberAsc(projectStoreyId,
                projectStackId);
    }

    public Optional<ProjectUnit> getProjectUnitById(Long id) {
        return projectUnitRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public ProjectUnit createProjectUnit(ProjectUnit projectUnit) {
        ProjectStorey storey = validateProjectStorey(projectUnit.getProjectStoreyId());
        ProjectStack stack = validateProjectStack(projectUnit.getProjectStackId());
        validateSameBlock(storey, stack);
        validateUniqueStoreyStackIntersection(projectUnit.getProjectStoreyId(), projectUnit.getProjectStackId(), null);
        validateUniqueUnitNumber(projectUnit.getProjectStoreyId(), projectUnit.getUnitNumber(), null);
        validateStreamMapping(storey, projectUnit.getProjectStreamId(), null);
        return projectUnitRepository.save(projectUnit);
    }

    @Transactional
    public ProjectUnit updateProjectUnit(Long id, ProjectUnit projectUnitDetails) {
        return projectUnitRepository.findById(Objects.requireNonNull(id, "id cannot be null")).map(projectUnit -> {
            ProjectStorey storey = validateProjectStorey(projectUnitDetails.getProjectStoreyId());
            ProjectStack stack = validateProjectStack(projectUnitDetails.getProjectStackId());
            validateSameBlock(storey, stack);

            projectUnit.setProjectStoreyId(projectUnitDetails.getProjectStoreyId());
            projectUnit.setProjectStackId(projectUnitDetails.getProjectStackId());
            projectUnit.setUnitName(projectUnitDetails.getUnitName());
            projectUnit.setUnitDescription(projectUnitDetails.getUnitDescription());
            projectUnit.setUnitNumber(projectUnitDetails.getUnitNumber());
            projectUnit.setProjectStreamId(projectUnitDetails.getProjectStreamId());
            projectUnit.setStatus(projectUnitDetails.getStatus());

            validateUniqueStoreyStackIntersection(projectUnit.getProjectStoreyId(), projectUnit.getProjectStackId(),
                    projectUnit.getProjectUnitId());
            validateUniqueUnitNumber(projectUnit.getProjectStoreyId(), projectUnit.getUnitNumber(),
                    projectUnit.getProjectUnitId());
            validateStreamMapping(storey, projectUnit.getProjectStreamId(), projectUnit.getProjectUnitId());
            return projectUnitRepository.save(projectUnit);
        }).orElseThrow(() -> new RuntimeException("ProjectUnit not found with id " + id));
    }

    @Transactional
    public void deleteProjectUnit(Long id) {
        ProjectUnit unit = projectUnitRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElseThrow(() -> new RuntimeException("ProjectUnit not found with id " + id));

        List<ProjectUnitWork> works = projectUnitWorkRepository.findByProjectUnitId(unit.getProjectUnitId());
        projectUnitWorkRepository.deleteAll(new ArrayList<>(works));
        projectUnitRepository.delete(unit);
    }

    private ProjectStorey validateProjectStorey(Long projectStoreyId) {
        if (projectStoreyId == null) {
            throw new IllegalArgumentException("projectStoreyId is required");
        }

        return projectStoreyRepository.findById(Objects.requireNonNull(projectStoreyId, "projectStoreyId cannot be null"))
                .orElseThrow(() -> new IllegalArgumentException("ProjectStorey not found with id " + projectStoreyId));
    }

    private ProjectStack validateProjectStack(Long projectStackId) {
        if (projectStackId == null) {
            throw new IllegalArgumentException("projectStackId is required");
        }

        return projectStackRepository.findById(Objects.requireNonNull(projectStackId, "projectStackId cannot be null"))
                .orElseThrow(() -> new IllegalArgumentException("ProjectStack not found with id " + projectStackId));
    }

    private void validateUniqueUnitNumber(Long projectStoreyId, Long unitNumber, Long currentId) {
        if (projectStoreyId == null || unitNumber == null) {
            return;
        }

        for (ProjectUnit existing : projectUnitRepository.findByProjectStoreyIdAndUnitNumber(projectStoreyId, unitNumber)) {
            if (currentId == null || !currentId.equals(existing.getProjectUnitId())) {
                throw new IllegalArgumentException("Unit number must be unique within a storey");
            }
        }
    }

    private void validateUniqueStoreyStackIntersection(Long projectStoreyId, Long projectStackId, Long currentId) {
        if (projectStoreyId == null || projectStackId == null) {
            return;
        }

        for (ProjectUnit existing : projectUnitRepository.findByProjectStoreyIdAndProjectStackId(projectStoreyId,
                projectStackId)) {
            if (currentId == null || !currentId.equals(existing.getProjectUnitId())) {
                throw new IllegalArgumentException("A unit already exists for this storey-stack intersection");
            }
        }
    }

    private void validateSameBlock(ProjectStorey storey, ProjectStack stack) {
        if (storey == null || stack == null) {
            return;
        }

        if (storey.getProjectBlockId() == null || stack.getProjectBlockId() == null
                || !storey.getProjectBlockId().equals(stack.getProjectBlockId())) {
            throw new IllegalArgumentException("projectStoreyId and projectStackId must belong to the same block");
        }
    }

    private void validateStreamMapping(ProjectStorey storey, Long projectStreamId, Long currentUnitId) {
        if (projectStreamId == null) {
            return;
        }

        ProjectStream projectStream = projectStreamRepository.findById(projectStreamId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectStream not found with id " + projectStreamId));

        String projectCode = resolveProjectCode(storey);
        if (projectStream.getProjectCode() != null && !projectStream.getProjectCode().equals(projectCode)) {
            throw new IllegalArgumentException("projectStreamId must belong to the same project as the unit");
        }

        for (ProjectUnit existing : projectUnitRepository.findByProjectStreamId(projectStreamId)) {
            if (currentUnitId == null || !currentUnitId.equals(existing.getProjectUnitId())) {
                throw new IllegalArgumentException("Each project stream can be mapped to only one unit within a project");
            }
        }
    }

    private String resolveProjectCode(ProjectStorey storey) {
        if (storey == null || storey.getProjectBlockId() == null) {
            return null;
        }

        ProjectBlock block = projectBlockRepository.findById(Objects.requireNonNull(storey.getProjectBlockId(), "projectBlockId cannot be null"))
                .orElseThrow(() -> new IllegalArgumentException("ProjectBlock not found with id " + storey.getProjectBlockId()));
        return block.getProjectCode();
    }
}
