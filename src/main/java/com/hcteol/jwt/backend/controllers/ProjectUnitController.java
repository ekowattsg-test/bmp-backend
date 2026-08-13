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

import com.hcteol.jwt.backend.entities.ProjectUnit;
import com.hcteol.jwt.backend.services.ProjectUnitService;

@RestController
@RequestMapping("/api/projectunits")
public class ProjectUnitController {

    @Autowired
    private ProjectUnitService projectUnitService;

    @GetMapping("/storey/{projectStoreyId}")
    public List<ProjectUnit> getProjectUnitsByStoreyId(@PathVariable Long projectStoreyId) {
        return projectUnitService.getProjectUnitsByStoreyId(projectStoreyId);
    }

    @GetMapping("/project/{projectCode}")
    public List<ProjectUnit> getProjectUnitsByProjectCode(@PathVariable String projectCode) {
        return projectUnitService.getProjectUnitsByProjectCode(projectCode);
    }

    @GetMapping("/stack/{projectStackId}")
    public List<ProjectUnit> getProjectUnitsByStackId(@PathVariable Long projectStackId) {
        return projectUnitService.getProjectUnitsByStackId(projectStackId);
    }

    @GetMapping("/intersection/{projectStoreyId}/{projectStackId}")
    public List<ProjectUnit> getProjectUnitsByIntersection(
            @PathVariable Long projectStoreyId,
            @PathVariable Long projectStackId) {
        return projectUnitService.getProjectUnitsByIntersection(projectStoreyId, projectStackId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectUnit> getProjectUnitById(@PathVariable Long id) {
        return projectUnitService.getProjectUnitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectUnit> createProjectUnit(@RequestBody ProjectUnit projectUnit) {
        try {
            return ResponseEntity.ok(projectUnitService.createProjectUnit(projectUnit));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectUnit> updateProjectUnit(@PathVariable Long id, @RequestBody ProjectUnit projectUnit) {
        try {
            return ResponseEntity.ok(projectUnitService.updateProjectUnit(id, projectUnit));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectUnit(@PathVariable Long id) {
        projectUnitService.deleteProjectUnit(id);
        return ResponseEntity.noContent().build();
    }
}
