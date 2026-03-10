package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.staffSkillProfile;
import com.hcteol.jwt.backend.services.StaffSkillProfileService;

@RestController
@RequestMapping("/api/staffskillprofiles")
public class StaffSkillProfileController {

    @Autowired
    private StaffSkillProfileService staffSkillProfileService;

    @PostMapping()
    public staffSkillProfile createStaffSkillProfile(@RequestBody staffSkillProfile staffSkillProfile) {
        return staffSkillProfileService.addStaffSkillProfile(staffSkillProfile);
    }

    @GetMapping()
    public List<staffSkillProfile> getAllStaffSkillProfiles() {
        return staffSkillProfileService.getAllStaffSkillProfiles();
    }

    @GetMapping("/{staffSkillProfileId}")
    public staffSkillProfile getStaffSkillProfileById(@PathVariable Long staffSkillProfileId) {
        return staffSkillProfileService.getStaffSkillProfileById(staffSkillProfileId);
    }

    @PutMapping("/{staffSkillProfileId}")
    public staffSkillProfile updateStaffSkillProfile(@PathVariable Long staffSkillProfileId,
            @RequestBody staffSkillProfile staffSkillProfileDetails) {
        return staffSkillProfileService.updateStaffSkillProfile(staffSkillProfileId, staffSkillProfileDetails);
    }

    @DeleteMapping("/{staffSkillProfileId}")
    public void deleteStaffSkillProfile(@PathVariable Long staffSkillProfileId) {
        staffSkillProfileService.deleteStaffSkillProfile(staffSkillProfileId);
    }

    @GetMapping("/staff/{staffName}")
    public List<staffSkillProfile> getStaffSkillProfilesByStaffName(@PathVariable String staffName) {
        return staffSkillProfileService.getStaffSkillProfilesByStaffName(staffName);
    }

    @GetMapping("/skill/{staffSkillId}")
    public List<staffSkillProfile> getStaffSkillProfilesByStaffSkillId(@PathVariable Long staffSkillId) {
        return staffSkillProfileService.getStaffSkillProfilesByStaffSkillId(staffSkillId);
    }
}
