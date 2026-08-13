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
import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.entities.ProjectUnitWork;
import com.hcteol.jwt.backend.repositories.ProjectBlockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStackRepository;
import com.hcteol.jwt.backend.repositories.ProjectStoreyRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitRepository;
import com.hcteol.jwt.backend.repositories.ProjectUnitWorkRepository;

@Service
public class ProjectBlockService {

    @Autowired
    private ProjectBlockRepository projectBlockRepository;

    @Autowired
    private ProjectStoreyRepository projectStoreyRepository;

    @Autowired
    private ProjectStackRepository projectStackRepository;

    @Autowired
    private ProjectUnitRepository projectUnitRepository;

    @Autowired
    private ProjectUnitWorkRepository projectUnitWorkRepository;

    public List<ProjectBlock> getProjectBlocksByProjectCode(String projectCode) {
        if (projectCode == null || projectCode.isBlank()) {
            return projectBlockRepository.findAll();
        }
        return projectBlockRepository.findByProjectCodeOrderByBlockNumberAsc(projectCode);
    }

    public Optional<ProjectBlock> getProjectBlockById(Long id) {
        return projectBlockRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public ProjectBlock createProjectBlock(ProjectBlock projectBlock) {
        validateUniqueBlockNumber(projectBlock.getProjectCode(), projectBlock.getBlockNumber(), null);
        return projectBlockRepository.save(projectBlock);
    }

    @Transactional
    public ProjectBlock updateProjectBlock(Long id, ProjectBlock projectBlockDetails) {
        return projectBlockRepository.findById(Objects.requireNonNull(id, "id cannot be null")).map(projectBlock -> {
            projectBlock.setProjectCode(projectBlockDetails.getProjectCode());
            projectBlock.setBlockName(projectBlockDetails.getBlockName());
            projectBlock.setBlockDescription(projectBlockDetails.getBlockDescription());
            projectBlock.setBlockNumber(projectBlockDetails.getBlockNumber());
            projectBlock.setStatus(projectBlockDetails.getStatus());
            validateUniqueBlockNumber(projectBlock.getProjectCode(), projectBlock.getBlockNumber(), projectBlock.getProjectBlockId());
            return projectBlockRepository.save(projectBlock);
        }).orElseThrow(() -> new RuntimeException("ProjectBlock not found with id " + id));
    }

    @Transactional
    public void deleteProjectBlock(Long id) {
        ProjectBlock projectBlock = projectBlockRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElseThrow(() -> new RuntimeException("ProjectBlock not found with id " + id));

        List<ProjectStorey> storeys = projectStoreyRepository.findByProjectBlockId(projectBlock.getProjectBlockId());
        List<ProjectStack> stacks = projectStackRepository.findByProjectBlockId(projectBlock.getProjectBlockId());
        for (ProjectStorey storey : storeys) {
            deleteStoreyChildren(storey.getProjectStoreyId());
        }
        for (ProjectStack stack : stacks) {
            deleteStackChildren(stack.getProjectStackId());
        }
        projectStoreyRepository.deleteAll(new ArrayList<>(storeys));
        projectStackRepository.deleteAll(new ArrayList<>(stacks));
        projectBlockRepository.delete(projectBlock);
    }

    private void deleteStoreyChildren(Long projectStoreyId) {
        List<ProjectUnit> units = projectUnitRepository.findByProjectStoreyId(projectStoreyId);
        for (ProjectUnit unit : units) {
            List<ProjectUnitWork> works = projectUnitWorkRepository.findByProjectUnitId(unit.getProjectUnitId());
            projectUnitWorkRepository.deleteAll(new ArrayList<>(works));
        }
        projectUnitRepository.deleteAll(new ArrayList<>(units));
    }

    private void deleteStackChildren(Long projectStackId) {
        List<ProjectUnit> units = projectUnitRepository.findByProjectStackId(projectStackId);
        for (ProjectUnit unit : units) {
            List<ProjectUnitWork> works = projectUnitWorkRepository.findByProjectUnitId(unit.getProjectUnitId());
            projectUnitWorkRepository.deleteAll(new ArrayList<>(works));
        }
        projectUnitRepository.deleteAll(new ArrayList<>(units));
    }

    private void validateUniqueBlockNumber(String projectCode, Long blockNumber, Long currentId) {
        if (projectCode == null || projectCode.isBlank() || blockNumber == null) {
            return;
        }

        for (ProjectBlock existing : projectBlockRepository.findByProjectCodeAndBlockNumber(projectCode, blockNumber)) {
            if (currentId == null || !currentId.equals(existing.getProjectBlockId())) {
                throw new IllegalArgumentException("Block number must be unique within a project");
            }
        }
    }
}
