package com.hcteol.jwt.backend.controllers;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.ProjectManpowerDto;
import com.hcteol.jwt.backend.dtos.ProjectManpowerRegenerationResult;
import com.hcteol.jwt.backend.services.ProjectManpowerService;

@RestController
@RequestMapping("/api/projectmanpowers")
public class ProjectManpowerController {

    @Autowired
    private ProjectManpowerService projectManpowerService;

    @GetMapping
    public List<ProjectManpowerDto> getAllProjectManpowers() {
        return projectManpowerService.getAllProjectManpowers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectManpowerDto> getProjectManpowerById(@PathVariable Long id) {
        return projectManpowerService.getProjectManpowerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectManpowerDto> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectManpowerService.getProjectManpowersByTaskId(projectTaskId);
    }

    @GetMapping("/skill/{projectSkillId}")
    public List<ProjectManpowerDto> getByProjectSkillId(@PathVariable Long projectSkillId) {
        return projectManpowerService.getProjectManpowersBySkillId(projectSkillId);
    }

    @GetMapping("/staff/{staffId}")
    public List<ProjectManpowerDto> getByStaffId(@PathVariable String staffId) {
        return projectManpowerService.getProjectManpowersByStaffId(staffId);
    }

    @PostMapping
    public ProjectManpowerDto createProjectManpower(@RequestBody ProjectManpowerDto projectManpower) {
        return projectManpowerService.createProjectManpower(projectManpower);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectManpowerDto> updateProjectManpower(@PathVariable Long id, @RequestBody ProjectManpowerDto projectManpower) {
        try {
            ProjectManpowerDto updated = projectManpowerService.updateProjectManpower(id, projectManpower);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectManpower(@PathVariable Long id) {
        projectManpowerService.deleteProjectManpower(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/regenerate")
    public ResponseEntity<ProjectManpowerRegenerationResult> regenerateProjectManpowers(@RequestParam(required = false) LocalDate runDate) {
        return ResponseEntity.ok(projectManpowerService.regenerateProjectManpowers(runDate));
    }
}
