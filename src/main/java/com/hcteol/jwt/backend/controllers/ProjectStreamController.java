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

import com.hcteol.jwt.backend.dtos.ProjectStreamReplicationRequest;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.services.ProjectStreamDateRecalculationService;
import com.hcteol.jwt.backend.services.ProjectStreamReplicationService;
import com.hcteol.jwt.backend.services.ProjectStreamService;

@RestController
@RequestMapping("/api/projectstreams")
public class ProjectStreamController {

    @Autowired
    private ProjectStreamService projectStreamService;

    @Autowired
    private ProjectStreamDateRecalculationService projectStreamDateRecalculationService;

    @Autowired
    private ProjectStreamReplicationService projectStreamReplicationService;

    @GetMapping
    public List<ProjectStream> getAllProjectStreams(
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) String streamType) {
        return projectStreamService.getAllProjectStreams(projectCode, streamType);
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

    @PostMapping("/{id}/replicate")
    public ResponseEntity<ProjectStream> replicateProjectStream(
            @PathVariable Long id,
            @RequestBody ProjectStreamReplicationRequest request) {
        try {
            ProjectStream replicated = projectStreamReplicationService.replicateStream(id, request);
            return ResponseEntity.ok(replicated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

    @PostMapping("/recalculate-dates/{projectCode}")
    public ResponseEntity<Void> recalculateStreamDatesForProject(@PathVariable String projectCode) {
        projectStreamDateRecalculationService.recalculateStreamDatesForProject(projectCode);
        return ResponseEntity.noContent().build();
    }
}
