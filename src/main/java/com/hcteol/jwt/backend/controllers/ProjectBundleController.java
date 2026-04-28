package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.ProjectBundle;
import com.hcteol.jwt.backend.services.ProjectBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projectbundles")
public class ProjectBundleController {
    @Autowired
    private ProjectBundleService projectBundleService;

    @GetMapping
    public List<ProjectBundle> getAllProjectBundles() {
        return projectBundleService.getAllProjectBundles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectBundle> getProjectBundleById(@PathVariable Long id) {
        return projectBundleService.getProjectBundleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectBundle> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectBundleService.getProjectBundlesByTaskId(projectTaskId);
    }

    @PostMapping
    public ProjectBundle createProjectBundle(@RequestBody ProjectBundle projectBundle) {
        return projectBundleService.createProjectBundle(projectBundle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectBundle> updateProjectBundle(@PathVariable Long id, @RequestBody ProjectBundle projectBundle) {
        try {
            ProjectBundle updated = projectBundleService.updateProjectBundle(id, projectBundle);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectBundle(@PathVariable Long id) {
        projectBundleService.deleteProjectBundle(id);
        return ResponseEntity.noContent().build();
    }
}
