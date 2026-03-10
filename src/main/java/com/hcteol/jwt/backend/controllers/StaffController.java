package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.services.StaffService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/{staffName}")
    public Staff getStaffByName(@PathVariable String staffName) {
        return staffService.getStaffByName(staffName);
    }

    @PutMapping("/{staffName}")
    public Staff updateStaff(@PathVariable String staffName, @RequestBody Staff staffDetails) {
        return staffService.updateStaff(staffName, staffDetails);
    }

    @DeleteMapping("/{staffName}")
    public void deleteStaff(@PathVariable String staffName) {
        staffService.deleteStaff(staffName);
    }
}
