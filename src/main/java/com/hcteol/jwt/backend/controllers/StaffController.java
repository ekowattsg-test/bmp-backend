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

import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.services.StaffService;

@RestController
@RequestMapping("/api/staffs")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping()
    public Staff createStaff(@RequestBody Staff staff) {
        return staffService.addStaff(staff);
    }

    @GetMapping()
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<Staff> getStaffById(@PathVariable String staffId) {
        return staffService.getStaffById(staffId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mobile/{mobileNumber}")
    public ResponseEntity<Staff> getStaffByMobile(@PathVariable String mobileNumber) {
        return staffService.getStaffByMobileNumber(mobileNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<Staff> updateStaff(@PathVariable String staffId, @RequestBody Staff staffDetails) {
        Staff updated = staffService.updateStaff(staffId, staffDetails);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteStaff(@PathVariable String staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.noContent().build();
    }
}
