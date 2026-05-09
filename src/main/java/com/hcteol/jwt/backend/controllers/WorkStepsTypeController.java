package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.WorkStepsType;
import com.hcteol.jwt.backend.services.WorkStepsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/workstepstypes")
public class WorkStepsTypeController {

    private final WorkStepsTypeService service;

    @Autowired
    public WorkStepsTypeController(WorkStepsTypeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WorkStepsType> create(@RequestBody WorkStepsType workStepsType) {
        WorkStepsType saved = service.create(workStepsType);
        URI location = URI.create("/api/workstepstypes/" + saved.getWorkStepsTypeId());
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping
    public List<WorkStepsType> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkStepsType> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkStepsType> update(@PathVariable Long id, @RequestBody WorkStepsType workStepsType) {
        try {
            WorkStepsType updated = service.update(id, workStepsType);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
