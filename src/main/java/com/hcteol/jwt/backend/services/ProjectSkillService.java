package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProjectSkill;
import com.hcteol.jwt.backend.repositories.ProjectSkillRepository;

@Service
public class ProjectSkillService {

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    public List<ProjectSkill> getAllProjectSkills() {
        return projectSkillRepository.findAll();
    }

    public Optional<ProjectSkill> getProjectSkillById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return projectSkillRepository.findById(id);
    }

    public List<ProjectSkill> getProjectSkillsByTaskId(Long projectTaskId) {
        return projectSkillRepository.findByProjectTaskId(projectTaskId);
    }

    public ProjectSkill createProjectSkill(ProjectSkill projectSkill) {
        Objects.requireNonNull(projectSkill, "projectSkill cannot be null");
        return projectSkillRepository.save(projectSkill);
    }

    public ProjectSkill updateProjectSkill(Long id, ProjectSkill projectSkillDetails) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Objects.requireNonNull(projectSkillDetails, "projectSkillDetails cannot be null");
        return projectSkillRepository.findById(id).map(projectSkill -> {
            projectSkill.setProjectTaskId(projectSkillDetails.getProjectTaskId());
            projectSkill.setSkillId(projectSkillDetails.getSkillId());
            projectSkill.setUnit(projectSkillDetails.getUnit());
            return projectSkillRepository.save(projectSkill);
        }).orElseThrow(() -> new RuntimeException("ProjectSkill not found with id " + id));
    }

    public void deleteProjectSkill(Long id) {
        if (id == null) {
            return;
        }
        projectSkillRepository.deleteById(id);
    }
}
