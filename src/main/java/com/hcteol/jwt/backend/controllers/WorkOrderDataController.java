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

import com.hcteol.jwt.backend.entities.WorkOrderData;
import com.hcteol.jwt.backend.services.WorkOrderDataService;

@RestController
@RequestMapping("/api/workorder-data")
public class WorkOrderDataController {

    @Autowired
    private WorkOrderDataService service;

    @PostMapping
    public WorkOrderData create(@RequestBody WorkOrderData data) {
        return service.create(data);
    }

    @GetMapping
    public List<WorkOrderData> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderData> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderData> update(@PathVariable Long id, @RequestBody WorkOrderData data) {
        WorkOrderData updated = service.update(id, data);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
