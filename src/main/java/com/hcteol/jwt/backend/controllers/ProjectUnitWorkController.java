package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.ProjectUnitWork;
import com.hcteol.jwt.backend.services.ProjectUnitWorkService;

@RestController
@RequestMapping("/api/projectunitworks")
public class ProjectUnitWorkController {

    @Autowired
    private ProjectUnitWorkService projectUnitWorkService;

    @GetMapping("/unit/{projectUnitId}")
    public List<ProjectUnitWork> getProjectUnitWorksByUnitId(@PathVariable Long projectUnitId) {
        return projectUnitWorkService.getProjectUnitWorksByUnitId(projectUnitId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectUnitWork> getProjectUnitWorkById(@PathVariable Long id) {
        return projectUnitWorkService.getProjectUnitWorkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectUnitWork> createProjectUnitWork(@RequestBody ProjectUnitWork projectUnitWork) {
        try {
            return ResponseEntity.ok(projectUnitWorkService.createProjectUnitWork(projectUnitWork));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectUnitWork(@PathVariable Long id) {
        projectUnitWorkService.deleteProjectUnitWork(id);
        return ResponseEntity.noContent().build();
    }
}
