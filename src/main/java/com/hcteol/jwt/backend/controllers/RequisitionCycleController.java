package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.hcteol.jwt.backend.entities.RequisitionCycle;
import com.hcteol.jwt.backend.services.RequisitionCycleService;

@RestController
@RequestMapping("/api/requisitioncycles")
public class RequisitionCycleController {

    @Autowired
    private RequisitionCycleService requisitionCycleService;

    @GetMapping
    public List<RequisitionCycle> getRequisitionCycles(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return requisitionCycleService.getRequisitionCyclesByStatus(status.trim());
        }
        return requisitionCycleService.getAllRequisitionCycles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequisitionCycle> getRequisitionCycleById(@PathVariable Long id) {
        return requisitionCycleService.getRequisitionCycleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RequisitionCycle> createRequisitionCycle(@RequestBody RequisitionCycle requisitionCycle) {
        RequisitionCycle created = requisitionCycleService.addRequisitionCycle(requisitionCycle);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequisitionCycle> updateRequisitionCycle(@PathVariable Long id,
            @RequestBody RequisitionCycle details) {
        RequisitionCycle updated = requisitionCycleService.updateRequisitionCycle(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequisitionCycle(@PathVariable Long id) {
        requisitionCycleService.deleteRequisitionCycle(id);
        return ResponseEntity.noContent().build();
    }
}
