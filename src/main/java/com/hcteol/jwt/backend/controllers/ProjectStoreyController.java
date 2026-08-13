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

import com.hcteol.jwt.backend.entities.ProjectStorey;
import com.hcteol.jwt.backend.services.ProjectStoreyService;

@RestController
@RequestMapping("/api/projectstoreys")
public class ProjectStoreyController {

    @Autowired
    private ProjectStoreyService projectStoreyService;

    @GetMapping("/block/{projectBlockId}")
    public List<ProjectStorey> getProjectStoreysByBlockId(@PathVariable Long projectBlockId) {
        return projectStoreyService.getProjectStoreysByBlockId(projectBlockId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectStorey> getProjectStoreyById(@PathVariable Long id) {
        return projectStoreyService.getProjectStoreyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectStorey> createProjectStorey(@RequestBody ProjectStorey projectStorey) {
        try {
            return ResponseEntity.ok(projectStoreyService.createProjectStorey(projectStorey));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectStorey> updateProjectStorey(@PathVariable Long id, @RequestBody ProjectStorey projectStorey) {
        try {
            return ResponseEntity.ok(projectStoreyService.updateProjectStorey(id, projectStorey));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectStorey(@PathVariable Long id) {
        projectStoreyService.deleteProjectStorey(id);
        return ResponseEntity.noContent().build();
    }
}
