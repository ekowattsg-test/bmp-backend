package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.ProjectLeader;
import com.hcteol.jwt.backend.services.ProjectLeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projectleaders")
public class ProjectLeaderController {

    @Autowired
    private ProjectLeaderService projectLeaderService;

    @GetMapping
    public List<ProjectLeader> getAllProjectLeaders() {
        return projectLeaderService.getAllProjectLeaders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectLeader> getProjectLeaderById(@PathVariable Long id) {
        return projectLeaderService.getProjectLeaderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectCode}")
    public List<ProjectLeader> getByProjectCode(@PathVariable String projectCode) {
        return projectLeaderService.getProjectLeadersByProjectCode(projectCode);
    }

    @GetMapping("/staff/{projectLeaderStaffId}")
    public List<ProjectLeader> getByStaffId(@PathVariable String projectLeaderStaffId) {
        return projectLeaderService.getProjectLeadersByStaffId(projectLeaderStaffId);
    }

    @GetMapping("/active/{active}")
    public List<ProjectLeader> getByActive(@PathVariable Integer active) {
        return projectLeaderService.getProjectLeadersByActive(active);
    }

    @PostMapping
    public ProjectLeader createProjectLeader(@RequestBody ProjectLeader projectLeader) {
        return projectLeaderService.createProjectLeader(projectLeader);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectLeader> updateProjectLeader(@PathVariable Long id, @RequestBody ProjectLeader projectLeader) {
        try {
            ProjectLeader updated = projectLeaderService.updateProjectLeader(id, projectLeader);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectLeader(@PathVariable Long id) {
        projectLeaderService.deleteProjectLeader(id);
        return ResponseEntity.noContent().build();
    }
}
