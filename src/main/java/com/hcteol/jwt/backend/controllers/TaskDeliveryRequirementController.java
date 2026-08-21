package com.hcteol.jwt.backend.controllers;

import java.util.List;
import java.util.Map;

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

import com.hcteol.jwt.backend.entities.TaskDeliveryRequirement;
import com.hcteol.jwt.backend.services.TaskDeliveryRequirementService;

@RestController
@RequestMapping("/api/taskdeliveryrequirements")
public class TaskDeliveryRequirementController {

    @Autowired
    private TaskDeliveryRequirementService taskDeliveryRequirementService;

    @GetMapping
    public List<TaskDeliveryRequirement> getTaskDeliveryRequirements(
            @RequestParam(required = false) String weekStartDate,
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deliveryOrderId) {
        return taskDeliveryRequirementService.getTaskDeliveryRequirements(
                weekStartDate,
                projectCode,
                productId,
                status,
                deliveryOrderId);
    }

    @GetMapping("/week/{weekStartDate}")
    public List<TaskDeliveryRequirement> getTaskDeliveryRequirementsByWeek(
            @PathVariable String weekStartDate) {
        return taskDeliveryRequirementService.getTaskDeliveryRequirementsByWeekStartDate(weekStartDate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDeliveryRequirement> getTaskDeliveryRequirementById(@PathVariable Long id) {
        return taskDeliveryRequirementService.getTaskDeliveryRequirementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskDeliveryRequirement> createTaskDeliveryRequirement(
            @RequestBody TaskDeliveryRequirement requirement) {
        TaskDeliveryRequirement created = taskDeliveryRequirementService.addTaskDeliveryRequirement(requirement);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractTaskInventoryForWeek(
            @RequestParam(required = false) String weekStartDate) {
        try {
            Map<String, Object> result = taskDeliveryRequirementService.extractTaskInventoryForWeek(weekStartDate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/generate-do")
    public ResponseEntity<?> generateDeliveryOrdersFromSelectedRequirements(
            @RequestBody List<TaskDeliveryRequirement> requirements) {
        try {
            Map<String, Object> result = taskDeliveryRequirementService
                    .generateDeliveryOrdersFromSelectedRequirements(requirements);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDeliveryRequirement> updateTaskDeliveryRequirement(
            @PathVariable Long id,
            @RequestBody TaskDeliveryRequirement details) {
        TaskDeliveryRequirement updated = taskDeliveryRequirementService.updateTaskDeliveryRequirement(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskDeliveryRequirement(@PathVariable Long id) {
        taskDeliveryRequirementService.deleteTaskDeliveryRequirement(id);
        return ResponseEntity.noContent().build();
    }
}
