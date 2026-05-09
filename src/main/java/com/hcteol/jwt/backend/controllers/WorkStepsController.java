package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.WorkSteps;
import com.hcteol.jwt.backend.services.WorkStepsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worksteps")
public class WorkStepsController {

    @Autowired
    private WorkStepsService workStepsService;

    @GetMapping
    public List<WorkSteps> getAllWorkSteps() {
        return workStepsService.getAllWorkSteps();
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
