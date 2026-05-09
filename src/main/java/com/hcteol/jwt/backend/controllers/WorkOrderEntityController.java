package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.WorkOrderEntity;
import com.hcteol.jwt.backend.services.WorkOrderEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workorder-entities")
public class WorkOrderEntityController {

    @Autowired
    private WorkOrderEntityService service;

    @PostMapping
    public WorkOrderEntity create(@RequestBody WorkOrderEntity workOrder) {
        return service.create(workOrder);
    }

    @GetMapping
    public List<WorkOrderEntity> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderEntity> get(@PathVariable String id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderEntity> update(@PathVariable String id, @RequestBody WorkOrderEntity workOrder) {
        WorkOrderEntity updated = service.update(id, workOrder);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
