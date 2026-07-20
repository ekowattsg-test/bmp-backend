package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.hcteol.jwt.backend.dtos.ProjectTaskProgressUpdateResponse;
import com.hcteol.jwt.backend.entities.ProjectTaskProgress;
import com.hcteol.jwt.backend.services.ProjectTaskProgressService;

@RestController
@RequestMapping("/api/projecttaskprogresses")
public class ProjectTaskProgressController {

    @Autowired
    private ProjectTaskProgressService projectTaskProgressService;

    @GetMapping
    public List<ProjectTaskProgress> getProjectTaskProgresses(
            @RequestParam(required = false) Long projectTaskId,
            @RequestParam(required = false) String progressDate,
            @RequestParam(required = false) String executedBy,
            @RequestParam(required = false) String reportedBy,
            @RequestParam(required = false) String marker,
            @RequestParam(required = false) Integer completed) {
        if (projectTaskId != null && progressDate != null && !progressDate.isBlank()) {
            return projectTaskProgressService
                    .getProjectTaskProgressByProjectTaskIdAndProgressDate(projectTaskId, progressDate.trim())
                    .map(List::of)
                    .orElse(List.of());
        }
        if (projectTaskId != null && completed != null) {
            return projectTaskProgressService.getProjectTaskProgressesByProjectTaskIdAndCompleted(projectTaskId,
                    completed);
        }
        if (projectTaskId != null && marker != null && !marker.isBlank()) {
            return projectTaskProgressService.getProjectTaskProgressesByProjectTaskIdAndMarker(projectTaskId,
                    marker.trim());
        }
        if (projectTaskId != null) {
            return projectTaskProgressService.getProjectTaskProgressesByProjectTaskId(projectTaskId);
        }
        if (completed != null) {
            return projectTaskProgressService.getProjectTaskProgressesByCompleted(completed);
        }
        if (marker != null && !marker.isBlank()) {
            return projectTaskProgressService.getProjectTaskProgressesByMarker(marker.trim());
        }
        if (executedBy != null && !executedBy.isBlank()) {
            return projectTaskProgressService.getProjectTaskProgressesByExecutedBy(executedBy.trim());
        }
        if (reportedBy != null && !reportedBy.isBlank()) {
            return projectTaskProgressService.getProjectTaskProgressesByReportedBy(reportedBy.trim());
        }
        return projectTaskProgressService.getAllProjectTaskProgresses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectTaskProgress> getProjectTaskProgressById(@PathVariable Long id) {
        return projectTaskProgressService.getProjectTaskProgressById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectTaskProgress> getProjectTaskProgressesByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectTaskProgressService.getProjectTaskProgressesByProjectTaskId(projectTaskId);
    }

    @PostMapping
    public ResponseEntity<ProjectTaskProgress> createProjectTaskProgress(@RequestBody ProjectTaskProgress projectTaskProgress) {
        ProjectTaskProgress created = projectTaskProgressService.addProjectTaskProgress(projectTaskProgress);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectTaskProgressUpdateResponse> updateProjectTaskProgress(@PathVariable Long id,
            @RequestBody ProjectTaskProgress details) {
        ProjectTaskProgressUpdateResponse updated = projectTaskProgressService
                .updateProjectTaskProgressWithTaskSnapshot(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectTaskProgress(@PathVariable Long id) {
        projectTaskProgressService.deleteProjectTaskProgress(id);
        return ResponseEntity.noContent().build();
    }
}
