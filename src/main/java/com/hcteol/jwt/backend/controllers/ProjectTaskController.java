package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
