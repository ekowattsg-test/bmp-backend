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

import com.hcteol.jwt.backend.entities.ProjectAsset;
import com.hcteol.jwt.backend.services.ProjectAssetService;

@RestController
@RequestMapping("/api/projectassets")
public class ProjectAssetController {

    @Autowired
    private ProjectAssetService projectAssetService;

    @GetMapping
    public List<ProjectAsset> getAllProjectAssets() {
        return projectAssetService.getAllProjectAssets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectAsset> getProjectAssetById(@PathVariable Long id) {
        return projectAssetService.getProjectAssetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectAsset> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectAssetService.getProjectAssetsByTaskId(projectTaskId);
    }

    @PostMapping
    public ProjectAsset createProjectAsset(@RequestBody ProjectAsset projectAsset) {
        return projectAssetService.createProjectAsset(projectAsset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectAsset> updateProjectAsset(@PathVariable Long id, @RequestBody ProjectAsset projectAsset) {
        try {
            ProjectAsset updated = projectAssetService.updateProjectAsset(id, projectAsset);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectAsset(@PathVariable Long id) {
        projectAssetService.deleteProjectAsset(id);
        return ResponseEntity.noContent().build();
    }
}
