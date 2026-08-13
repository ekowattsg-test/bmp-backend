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

import com.hcteol.jwt.backend.entities.ProjectBlock;
import com.hcteol.jwt.backend.services.ProjectBlockService;

@RestController
@RequestMapping("/api/projectblocks")
public class ProjectBlockController {

    @Autowired
    private ProjectBlockService projectBlockService;

    @GetMapping("/project/{projectCode}")
    public List<ProjectBlock> getProjectBlocksByProjectCode(@PathVariable String projectCode) {
        return projectBlockService.getProjectBlocksByProjectCode(projectCode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectBlock> getProjectBlockById(@PathVariable Long id) {
        return projectBlockService.getProjectBlockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectBlock> createProjectBlock(@RequestBody ProjectBlock projectBlock) {
        try {
            return ResponseEntity.ok(projectBlockService.createProjectBlock(projectBlock));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectBlock> updateProjectBlock(@PathVariable Long id, @RequestBody ProjectBlock projectBlock) {
        try {
            return ResponseEntity.ok(projectBlockService.updateProjectBlock(id, projectBlock));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectBlock(@PathVariable Long id) {
        projectBlockService.deleteProjectBlock(id);
        return ResponseEntity.noContent().build();
    }
}
