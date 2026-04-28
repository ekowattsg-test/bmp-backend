package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.services.ProjectStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projectstreams")
public class ProjectStreamController {
    @Autowired
    private ProjectStreamService projectStreamService;

    @GetMapping
    public List<ProjectStream> getAllProjectStreams() {
        return projectStreamService.getAllProjectStreams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectStream> getProjectStreamById(@PathVariable Long id) {
        return projectStreamService.getProjectStreamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectCode}")
    public List<ProjectStream> getByProjectCode(@PathVariable String projectCode) {
        return projectStreamService.getProjectStreamsByProjectCode(projectCode);
    }

    @PostMapping
    public ProjectStream createProjectStream(@RequestBody ProjectStream projectStream) {
        return projectStreamService.createProjectStream(projectStream);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectStream> updateProjectStream(@PathVariable Long id, @RequestBody ProjectStream projectStream) {
        try {
            ProjectStream updated = projectStreamService.updateProjectStream(id, projectStream);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectStream(@PathVariable Long id) {
        projectStreamService.deleteProjectStream(id);
        return ResponseEntity.noContent().build();
    }
}
