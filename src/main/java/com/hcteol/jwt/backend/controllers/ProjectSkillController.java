package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.ProjectSkillDto;
import com.hcteol.jwt.backend.services.ProjectSkillService;

@RestController
@RequestMapping("/api/projectskills")
public class ProjectSkillController {

    @Autowired
    private ProjectSkillService projectSkillService;

    @GetMapping
    public List<ProjectSkillDto> getAllProjectSkills() {
        return projectSkillService.getAllProjectSkills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectSkillDto> getProjectSkillById(@PathVariable Long id) {
        return projectSkillService.getProjectSkillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectSkillDto> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectSkillService.getProjectSkillsByTaskId(projectTaskId);
    }

    @PostMapping
    public ProjectSkillDto createProjectSkill(@RequestBody ProjectSkillDto projectSkill) {
        return projectSkillService.createProjectSkill(projectSkill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectSkillDto> updateProjectSkill(@PathVariable Long id, @RequestBody ProjectSkillDto projectSkill) {
        try {
            ProjectSkillDto updated = projectSkillService.updateProjectSkill(id, projectSkill);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectSkill(@PathVariable Long id) {
        projectSkillService.deleteProjectSkill(id);
        return ResponseEntity.noContent().build();
    }
}
