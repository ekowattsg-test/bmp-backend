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

import com.hcteol.jwt.backend.entities.StaffMeritProfile;
import com.hcteol.jwt.backend.services.StaffMeritProfileService;

@RestController
@RequestMapping("/api/staffmeritprofiles")
public class StaffMeritProfileController {

    @Autowired
    private StaffMeritProfileService staffMeritProfileService;

    @GetMapping
    public List<StaffMeritProfile> getAllStaffMeritProfiles(
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) Long staffMeritId) {
        return staffMeritProfileService.getAllStaffMeritProfiles(staffId, staffMeritId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffMeritProfile> getStaffMeritProfileById(@PathVariable Long id) {
        return staffMeritProfileService.getStaffMeritProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StaffMeritProfile createStaffMeritProfile(@RequestBody StaffMeritProfile staffMeritProfile) {
        return staffMeritProfileService.createStaffMeritProfile(staffMeritProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffMeritProfile> updateStaffMeritProfile(@PathVariable Long id, @RequestBody StaffMeritProfile staffMeritProfileDetails) {
        try {
            StaffMeritProfile updated = staffMeritProfileService.updateStaffMeritProfile(id, staffMeritProfileDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffMeritProfile(@PathVariable Long id) {
        staffMeritProfileService.deleteStaffMeritProfile(id);
        return ResponseEntity.noContent().build();
    }
}
