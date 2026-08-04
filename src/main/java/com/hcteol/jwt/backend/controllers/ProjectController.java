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

import com.hcteol.jwt.backend.dtos.ProjectNearbySearchDto;
import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.services.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<Project> getAllProjects(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long briefingId) {
        return projectService.getAllProjects(customerId, status, briefingId);
    }

    @GetMapping("/{projectCode}")
    public ResponseEntity<Project> getProjectByCode(@PathVariable String projectCode) {
        return projectService.getProjectByCode(projectCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @PostMapping("/nearby")
    public ResponseEntity<List<Project>> getNearbyProjects(@RequestBody ProjectNearbySearchDto request) {
        try {
            List<Project> projects = projectService.getProjectsNearCoordinate(request.getLatitude(), request.getLongitude());
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{projectCode}")
    public ResponseEntity<Project> updateProject(@PathVariable String projectCode, @RequestBody Project project) {
        try {
            Project updatedProject = projectService.updateProject(projectCode, project);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectCode}")
    public ResponseEntity<Void> deleteProject(@PathVariable String projectCode) {
        projectService.deleteProject(projectCode);
        return ResponseEntity.noContent().build();
    }
}
