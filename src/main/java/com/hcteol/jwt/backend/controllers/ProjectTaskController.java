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

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.services.ProjectTaskService;

@RestController
@RequestMapping("/api/projecttasks")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @GetMapping
    public List<ProjectTask> getAllProjectTasks() {
        return projectTaskService.getAllProjectTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectTask> getProjectTaskById(@PathVariable Long id) {
        return projectTaskService.getProjectTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stream/{projectStreamId}")
    public List<ProjectTask> getByProjectStreamId(@PathVariable Long projectStreamId) {
        return projectTaskService.getProjectTasksByStreamId(projectStreamId);
    }

    @PostMapping
    public ProjectTask createProjectTask(@RequestBody ProjectTask projectTask) {
        return projectTaskService.createProjectTask(projectTask);
    }

    @PostMapping("/calculate")
    public ResponseEntity<ProjectTask> calculateProjectTask(@RequestBody ProjectTask projectTask) {
        try {
            return ResponseEntity.ok(projectTaskService.calculateProjectTask(projectTask));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/recalculate/project/{projectCode}")
    public ResponseEntity<Void> recalculateProjectTasksByProjectCode(@PathVariable String projectCode) {
        try {
            projectTaskService.recalculateProjectTasksByProjectCode(projectCode);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectTask> updateProjectTask(@PathVariable Long id, @RequestBody ProjectTask projectTask) {
        try {
            ProjectTask updated = projectTaskService.updateProjectTask(id, projectTask);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectTask(@PathVariable Long id) {
        projectTaskService.deleteProjectTask(id);
        return ResponseEntity.noContent().build();
    }
}
