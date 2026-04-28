package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.ProjectManpower;
import com.hcteol.jwt.backend.services.ProjectManpowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projectmanpowers")
public class ProjectManpowerController {
    @Autowired
    private ProjectManpowerService projectManpowerService;

    @GetMapping
    public List<ProjectManpower> getAllProjectManpowers() {
        return projectManpowerService.getAllProjectManpowers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectManpower> getProjectManpowerById(@PathVariable Long id) {
        return projectManpowerService.getProjectManpowerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectManpower> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectManpowerService.getProjectManpowersByTaskId(projectTaskId);
    }

    @PostMapping
    public ProjectManpower createProjectManpower(@RequestBody ProjectManpower projectManpower) {
        return projectManpowerService.createProjectManpower(projectManpower);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectManpower> updateProjectManpower(@PathVariable Long id, @RequestBody ProjectManpower projectManpower) {
        try {
            ProjectManpower updated = projectManpowerService.updateProjectManpower(id, projectManpower);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectManpower(@PathVariable Long id) {
        projectManpowerService.deleteProjectManpower(id);
        return ResponseEntity.noContent().build();
    }
}
