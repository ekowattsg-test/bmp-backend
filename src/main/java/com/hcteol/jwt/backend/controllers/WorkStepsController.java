package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.WorkSteps;
import com.hcteol.jwt.backend.services.WorkStepsService;
import com.hcteol.jwt.backend.dtos.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worksteps")
public class WorkStepsController {

    @Autowired
    private WorkStepsService workStepsService;

    private static final Logger logger = LoggerFactory.getLogger(WorkStepsController.class);

    @PostMapping("/execute/{workOrderId}")
    public ResponseEntity<?> executeCurrentStep(@PathVariable String workOrderId) {
        try {
            workStepsService.performCurrentStep(workOrderId);
            return ResponseEntity.ok().body(java.util.Map.of("status", "ok"));
        } catch (java.util.NoSuchElementException ex) {
            ErrorResponse err = new ErrorResponse(404, "Not Found", ex.getMessage());
            logger.warn("Work order not found: {}", workOrderId);
            return ResponseEntity.status(404).body(err);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            ErrorResponse err = new ErrorResponse(400, "Bad Request", ex.getMessage());
            logger.warn("Invalid request for work order {}: {}", workOrderId, ex.getMessage());
            return ResponseEntity.status(400).body(err);
        } catch (Exception ex) {
            // log and return 500
            logger.error("Error executing step for {}: {}", workOrderId, ex.getMessage(), ex);
            ErrorResponse err = new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(err);
        }
    }

    @GetMapping
    public List<WorkSteps> getAllWorkSteps() {
        return workStepsService.getAllWorkSteps();
    }

    @GetMapping("/order/{workOrderId}")
    public List<WorkSteps> getWorkStepsByOrderId(@PathVariable String workOrderId) {
        return workStepsService.getWorkStepsByOrderId(workOrderId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkSteps> getWorkStepById(@PathVariable Long id) {
        return workStepsService.getWorkStepById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public WorkSteps createWorkStep(@RequestBody WorkSteps step) {
        return workStepsService.addWorkStep(step);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkSteps> updateWorkStep(@PathVariable Long id, @RequestBody WorkSteps step) {
        WorkSteps updated = workStepsService.updateWorkStep(id, step);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkStep(@PathVariable Long id) {
        workStepsService.deleteWorkStep(id);
        return ResponseEntity.noContent().build();
    }
}
