package com.hcteol.jwt.backend.controllers;

import java.net.URI;
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

import com.hcteol.jwt.backend.entities.ProjectTaskType;
import com.hcteol.jwt.backend.services.ProjectTaskTypeService;

@RestController
@RequestMapping("/api/projecttasktypes")
public class ProjectTaskTypeController {

    private final ProjectTaskTypeService projectTaskTypeService;

    @Autowired
    public ProjectTaskTypeController(ProjectTaskTypeService projectTaskTypeService) {
        this.projectTaskTypeService = projectTaskTypeService;
    }

    @GetMapping
    public List<ProjectTaskType> getAllProjectTaskTypes() {
        return projectTaskTypeService.getAllProjectTaskTypes();
    }

    @GetMapping("/{projectTaskCode}")
    public ResponseEntity<ProjectTaskType> getProjectTaskTypeByCode(@PathVariable String projectTaskCode) {
        return projectTaskTypeService.getProjectTaskTypeByCode(projectTaskCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectTaskType> createProjectTaskType(@RequestBody ProjectTaskType projectTaskType) {
        ProjectTaskType saved = projectTaskTypeService.createProjectTaskType(projectTaskType);
        URI location = URI.create("/api/projecttasktypes/" + saved.getProjectTaskCode());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{projectTaskCode}")
    public ResponseEntity<ProjectTaskType> updateProjectTaskType(@PathVariable String projectTaskCode,
            @RequestBody ProjectTaskType projectTaskType) {
        return projectTaskTypeService.updateProjectTaskType(projectTaskCode, projectTaskType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectTaskCode}")
    public ResponseEntity<Void> deleteProjectTaskType(@PathVariable String projectTaskCode) {
        return projectTaskTypeService.deleteProjectTaskType(projectTaskCode)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
