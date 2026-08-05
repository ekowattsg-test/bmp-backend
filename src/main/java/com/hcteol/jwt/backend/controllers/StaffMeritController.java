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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.StaffMerit;
import com.hcteol.jwt.backend.services.StaffMeritService;

@RestController
@RequestMapping("/api/staffmerits")
public class StaffMeritController {

    @Autowired
    private StaffMeritService staffMeritService;

    @GetMapping
    public List<StaffMerit> getAllStaffMerits(@RequestParam(required = false) String meritCategory) {
        return staffMeritService.getAllStaffMerits(meritCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffMerit> getStaffMeritById(@PathVariable Long id) {
        return staffMeritService.getStaffMeritById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StaffMerit createStaffMerit(@RequestBody StaffMerit staffMerit) {
        return staffMeritService.createStaffMerit(staffMerit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffMerit> updateStaffMerit(@PathVariable Long id, @RequestBody StaffMerit staffMeritDetails) {
        try {
            StaffMerit updated = staffMeritService.updateStaffMerit(id, staffMeritDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffMerit(@PathVariable Long id) {
        staffMeritService.deleteStaffMerit(id);
        return ResponseEntity.noContent().build();
    }
}
