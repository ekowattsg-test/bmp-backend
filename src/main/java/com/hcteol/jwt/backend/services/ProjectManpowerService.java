package com.hcteol.jwt.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.ProjectManpowerDto;
import com.hcteol.jwt.backend.dtos.ProjectManpowerRegenerationResult;
import com.hcteol.jwt.backend.entities.ProjectManpower;
import com.hcteol.jwt.backend.repositories.ProjectManpowerRepository;
import com.hcteol.jwt.backend.repositories.ProjectSkillRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectManpowerService {

    @Autowired
    private ProjectManpowerRepository projectManpowerRepository;

    @Autowired
    private ProjectManpowerRegenerationService projectManpowerRegenerationService;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    public List<ProjectManpowerDto> getAllProjectManpowers() {
        return projectManpowerRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<ProjectManpowerDto> getProjectManpowerById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return projectManpowerRepository.findById(id).map(this::toDto);
    }

    public List<ProjectManpowerDto> getProjectManpowersByTaskId(Long projectTaskId) {
        return projectManpowerRepository.findByProjectTaskId(projectTaskId).stream().map(this::toDto).toList();
    }

    public List<ProjectManpowerDto> getProjectManpowersBySkillId(Long projectSkillId) {
        return projectManpowerRepository.findByProjectSkillId(projectSkillId).stream().map(this::toDto).toList();
    }

    public List<ProjectManpowerDto> getProjectManpowersByStaffId(String staffId) {
        return projectManpowerRepository.findByStaffId(staffId).stream().map(this::toDto).toList();
    }

    @Transactional
    public ProjectManpowerDto createProjectManpower(ProjectManpowerDto projectManpowerDto) {
        Objects.requireNonNull(projectManpowerDto, "projectManpowerDto cannot be null");
        ProjectManpower projectManpower = Objects.requireNonNull(
                toEntity(projectManpowerDto),
                "toEntity(projectManpowerDto) returned null");
        return Optional.ofNullable(projectManpowerRepository.save(projectManpower))
                .map(saved -> {
                    markParentTaskManpowerTouched(saved.getProjectTaskId(), saved.getProjectSkillId());
                    return saved;
                })
                .map(this::toDto)
                .orElseThrow(() -> new IllegalStateException("projectManpowerRepository.save returned null"));
    }

    @Transactional
    public ProjectManpowerDto updateProjectManpower(Long id, ProjectManpowerDto projectManpowerDetails) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Objects.requireNonNull(projectManpowerDetails, "projectManpowerDetails cannot be null");
        return projectManpowerRepository.findById(id).map(projectManpower -> {
            projectManpower.setProjectTaskId(projectManpowerDetails.getProjectTaskId());
            projectManpower.setProjectSkillId(projectManpowerDetails.getProjectSkillId());
            projectManpower.setWorkDate(projectManpowerDetails.getWorkDate());
            projectManpower.setStaffId(projectManpowerDetails.getStaffId());
            projectManpower.setLoading(projectManpowerDetails.getLoading());
            ProjectManpower saved = projectManpowerRepository.save(projectManpower);
            markParentTaskManpowerTouched(saved.getProjectTaskId(), saved.getProjectSkillId());
            return toDto(saved);
        }).orElseThrow(() -> new RuntimeException("ProjectManpower not found with id " + id));
    }

    public void deleteProjectManpower(Long id) {
        if (id == null) {
            return;
        }
        projectManpowerRepository.deleteById(id);
    }

    public ProjectManpowerRegenerationResult regenerateProjectManpowers() {
        return projectManpowerRegenerationService.regenerateForwardDatedManpower();
    }

    public ProjectManpowerRegenerationResult regenerateProjectManpowers(LocalDate runDate) {
        if (runDate == null) {
            return projectManpowerRegenerationService.regenerateForwardDatedManpower();
        }
        return projectManpowerRegenerationService.regenerateForwardDatedManpower(runDate);
    }

    private void markParentTaskManpowerTouched(Long projectTaskId, Long projectSkillId) {
        Long resolvedTaskId = projectTaskId;
        if (resolvedTaskId == null && projectSkillId != null) {
            resolvedTaskId = projectSkillRepository.findById(projectSkillId)
                    .map(projectSkill -> projectSkill.getProjectTaskId())
                    .orElse(null);
        }

        if (resolvedTaskId == null) {
            return;
        }

        projectTaskRepository.findById(resolvedTaskId).ifPresent(projectTask -> {
            Integer touched = projectTask.getManpowerTouched();
            if (touched != null && touched == 1) {
                return;
            }
            projectTask.setManpowerTouched(1);
            projectTaskRepository.save(projectTask);
        });
    }

    private ProjectManpowerDto toDto(ProjectManpower projectManpower) {
        return new ProjectManpowerDto(
                projectManpower.getProjectManpowerId(),
                projectManpower.getProjectTaskId(),
                projectManpower.getProjectSkillId(),
                projectManpower.getWorkDate(),
                projectManpower.getStaffId(),
                projectManpower.getLoading());
    }

    private ProjectManpower toEntity(ProjectManpowerDto projectManpowerDto) {
        ProjectManpower projectManpower = new ProjectManpower();
        projectManpower.setProjectManpowerId(projectManpowerDto.getProjectManpowerId());
        projectManpower.setProjectTaskId(projectManpowerDto.getProjectTaskId());
        projectManpower.setProjectSkillId(projectManpowerDto.getProjectSkillId());
        projectManpower.setWorkDate(projectManpowerDto.getWorkDate());
        projectManpower.setStaffId(projectManpowerDto.getStaffId());
        projectManpower.setLoading(projectManpowerDto.getLoading());
        return projectManpower;
    }
}
