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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.ProjectStreamBundle;
import com.hcteol.jwt.backend.services.ProjectStreamBundleService;

@RestController
@RequestMapping("/api/projectstreambundles")
public class ProjectStreamBundleController {

    @Autowired
    private ProjectStreamBundleService projectStreamBundleService;

    @GetMapping
    public List<ProjectStreamBundle> getProjectStreamBundles(
            @RequestParam(required = false) Long projectStreamId,
            @RequestParam(required = false) Long requisitionCycleId) {
        return projectStreamBundleService.getProjectStreamBundles(projectStreamId, requisitionCycleId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectStreamBundle> getProjectStreamBundleById(@PathVariable Long id) {
        return projectStreamBundleService.getProjectStreamBundleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stream/{projectStreamId}")
    public List<ProjectStreamBundle> getByProjectStreamId(@PathVariable Long projectStreamId) {
        return projectStreamBundleService.getProjectStreamBundlesByStreamId(projectStreamId);
    }

    @GetMapping("/requisitioncycle/{requisitionCycleId}")
    public List<ProjectStreamBundle> getByRequisitionCycleId(@PathVariable Long requisitionCycleId) {
        return projectStreamBundleService.getProjectStreamBundlesByRequisitionCycleId(requisitionCycleId);
    }

    @PostMapping
    public ProjectStreamBundle createProjectStreamBundle(@RequestBody ProjectStreamBundle projectStreamBundle) {
        return projectStreamBundleService.createProjectStreamBundle(projectStreamBundle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectStreamBundle> updateProjectStreamBundle(@PathVariable Long id,
            @RequestBody ProjectStreamBundle projectStreamBundle) {
        try {
            ProjectStreamBundle updated = projectStreamBundleService.updateProjectStreamBundle(id, projectStreamBundle);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectStreamBundle(@PathVariable Long id) {
        projectStreamBundleService.deleteProjectStreamBundle(id);
        return ResponseEntity.noContent().build();
    }
}
