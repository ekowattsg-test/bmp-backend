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

import com.hcteol.jwt.backend.entities.ProjectSkill;
import com.hcteol.jwt.backend.services.ProjectSkillService;

@RestController
@RequestMapping("/api/projectskills")
public class ProjectSkillController {

    @Autowired
    private ProjectSkillService projectSkillService;

    @GetMapping
    public List<ProjectSkill> getAllProjectSkills() {
        return projectSkillService.getAllProjectSkills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectSkill> getProjectSkillById(@PathVariable Long id) {
        return projectSkillService.getProjectSkillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectSkill> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectSkillService.getProjectSkillsByTaskId(projectTaskId);
    }

    @PostMapping
    public ProjectSkill createProjectSkill(@RequestBody ProjectSkill projectSkill) {
        return projectSkillService.createProjectSkill(projectSkill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectSkill> updateProjectSkill(@PathVariable Long id, @RequestBody ProjectSkill projectSkill) {
        try {
            ProjectSkill updated = projectSkillService.updateProjectSkill(id, projectSkill);
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
