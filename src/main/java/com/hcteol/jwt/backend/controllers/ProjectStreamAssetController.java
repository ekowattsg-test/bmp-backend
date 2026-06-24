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

import com.hcteol.jwt.backend.entities.ProjectStreamAsset;
import com.hcteol.jwt.backend.services.ProjectStreamAssetService;

@RestController
@RequestMapping("/api/projectstreamassets")
public class ProjectStreamAssetController {

    @Autowired
    private ProjectStreamAssetService projectStreamAssetService;

    @GetMapping
    public List<ProjectStreamAsset> getAllProjectStreamAssets() {
        return projectStreamAssetService.getAllProjectStreamAssets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectStreamAsset> getProjectStreamAssetById(@PathVariable Long id) {
        return projectStreamAssetService.getProjectStreamAssetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stream/{projectStreamId}")
    public List<ProjectStreamAsset> getByProjectStreamId(@PathVariable Long projectStreamId) {
        return projectStreamAssetService.getProjectStreamAssetsByStreamId(projectStreamId);
    }

    @PostMapping
    public ProjectStreamAsset createProjectStreamAsset(@RequestBody ProjectStreamAsset projectStreamAsset) {
        return projectStreamAssetService.createProjectStreamAsset(projectStreamAsset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectStreamAsset> updateProjectStreamAsset(@PathVariable Long id,
            @RequestBody ProjectStreamAsset projectStreamAsset) {
        try {
            ProjectStreamAsset updated = projectStreamAssetService.updateProjectStreamAsset(id, projectStreamAsset);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectStreamAsset(@PathVariable Long id) {
        projectStreamAssetService.deleteProjectStreamAsset(id);
        return ResponseEntity.noContent().build();
    }
}
