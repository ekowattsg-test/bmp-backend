package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.ProjectStock;
import com.hcteol.jwt.backend.services.ProjectStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projectstocks")
public class ProjectStockController {
    @Autowired
    private ProjectStockService projectStockService;

    @GetMapping
    public List<ProjectStock> getAllProjectStocks() {
        return projectStockService.getAllProjectStocks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectStock> getProjectStockById(@PathVariable Long id) {
        return projectStockService.getProjectStockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectStock> getByProjectTaskId(@PathVariable Long projectTaskId) {
        return projectStockService.getProjectStocksByTaskId(projectTaskId);
    }

    @PostMapping
    public ProjectStock createProjectStock(@RequestBody ProjectStock projectStock) {
        return projectStockService.createProjectStock(projectStock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectStock> updateProjectStock(@PathVariable Long id, @RequestBody ProjectStock projectStock) {
        try {
            ProjectStock updated = projectStockService.updateProjectStock(id, projectStock);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectStock(@PathVariable Long id) {
        projectStockService.deleteProjectStock(id);
        return ResponseEntity.noContent().build();
    }
}
