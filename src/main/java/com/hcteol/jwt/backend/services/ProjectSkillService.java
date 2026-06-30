package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.dtos.ProjectSkillDto;
import com.hcteol.jwt.backend.entities.ProjectSkill;
import com.hcteol.jwt.backend.repositories.ProjectSkillRepository;

@Service
public class ProjectSkillService {

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    public List<ProjectSkillDto> getAllProjectSkills() {
        return projectSkillRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<ProjectSkillDto> getProjectSkillById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return projectSkillRepository.findById(id).map(this::toDto);
    }

    public List<ProjectSkillDto> getProjectSkillsByTaskId(Long projectTaskId) {
        return projectSkillRepository.findByProjectTaskId(projectTaskId).stream().map(this::toDto).toList();
    }

    public ProjectSkillDto createProjectSkill(ProjectSkillDto projectSkillDto) {
        Objects.requireNonNull(projectSkillDto, "projectSkillDto cannot be null");
        ProjectSkill created = projectSkillRepository.save(toEntity(projectSkillDto));
        return toDto(created);
    }

    public ProjectSkillDto updateProjectSkill(Long id, ProjectSkillDto projectSkillDetails) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Objects.requireNonNull(projectSkillDetails, "projectSkillDetails cannot be null");
        return projectSkillRepository.findById(id).map(projectSkill -> {
            projectSkill.setProjectTaskId(projectSkillDetails.getProjectTaskId());
            projectSkill.setSkillId(projectSkillDetails.getSkillId());
            projectSkill.setUnit(projectSkillDetails.getUnit());
            return toDto(projectSkillRepository.save(projectSkill));
        }).orElseThrow(() -> new RuntimeException("ProjectSkill not found with id " + id));
    }

    public void deleteProjectSkill(Long id) {
        if (id == null) {
            return;
        }
        projectSkillRepository.deleteById(id);
    }

    private ProjectSkillDto toDto(ProjectSkill projectSkill) {
        return new ProjectSkillDto(
                projectSkill.getProjectSkillId(),
                projectSkill.getProjectTaskId(),
                projectSkill.getSkillId(),
                projectSkill.getUnit());
    }

    private ProjectSkill toEntity(ProjectSkillDto projectSkillDto) {
        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setProjectSkillId(projectSkillDto.getProjectSkillId());
        projectSkill.setProjectTaskId(projectSkillDto.getProjectTaskId());
        projectSkill.setSkillId(projectSkillDto.getSkillId());
        projectSkill.setUnit(projectSkillDto.getUnit());
        return projectSkill;
    }
}
