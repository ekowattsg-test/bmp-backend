package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.WorkOrderType;
import com.hcteol.jwt.backend.services.WorkOrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workordertypes")
public class WorkOrderTypeController {

    @Autowired
    private WorkOrderTypeService workOrderTypeService;

    @GetMapping
    public List<WorkOrderType> getAllWorkOrderTypes() {
        return workOrderTypeService.getAllWorkOrderTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderType> getWorkOrderTypeById(@PathVariable String id) {
        return workOrderTypeService.getWorkOrderTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public WorkOrderType createWorkOrderType(@RequestBody WorkOrderType type) {
        return workOrderTypeService.addWorkOrderType(type);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderType> updateWorkOrderType(@PathVariable String id, @RequestBody WorkOrderType type) {
        WorkOrderType updated = workOrderTypeService.updateWorkOrderType(id, type);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkOrderType(@PathVariable String id) {
        workOrderTypeService.deleteWorkOrderType(id);
        return ResponseEntity.noContent().build();
    }
}
