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

import com.hcteol.jwt.backend.entities.ProjectStack;
import com.hcteol.jwt.backend.services.ProjectStackService;

@RestController
@RequestMapping("/api/projectstacks")
public class ProjectStackController {

    @Autowired
    private ProjectStackService projectStackService;

    @GetMapping("/block/{projectBlockId}")
    public List<ProjectStack> getProjectStacksByBlockId(@PathVariable Long projectBlockId) {
        return projectStackService.getProjectStacksByBlockId(projectBlockId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectStack> getProjectStackById(@PathVariable Long id) {
        return projectStackService.getProjectStackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectStack> createProjectStack(@RequestBody ProjectStack projectStack) {
        try {
            return ResponseEntity.ok(projectStackService.createProjectStack(projectStack));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectStack> updateProjectStack(@PathVariable Long id, @RequestBody ProjectStack projectStack) {
        try {
            return ResponseEntity.ok(projectStackService.updateProjectStack(id, projectStack));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectStack(@PathVariable Long id) {
        projectStackService.deleteProjectStack(id);
        return ResponseEntity.noContent().build();
    }
}
