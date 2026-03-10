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

import com.hcteol.jwt.backend.entities.StaffSkill;
import com.hcteol.jwt.backend.services.StaffSkillService;

@RestController
@RequestMapping("/api/staffskills")
public class StaffSkillController {

    @Autowired
    private StaffSkillService staffSkillService;

    @PostMapping()
    public StaffSkill createStaffSkill(@RequestBody StaffSkill staffSkill) {
        return staffSkillService.addStaffSkill(staffSkill);
    }

    @GetMapping()
    public List<StaffSkill> getAllStaffSkills() {
        return staffSkillService.getAllStaffSkills();
    }

    @GetMapping("/{staffSkillId}")
    public StaffSkill getStaffSkillById(@PathVariable Long staffSkillId) {
        return staffSkillService.getStaffSkillById(staffSkillId);
    }

    @PutMapping("/{staffSkillId}")
    public StaffSkill updateStaffSkill(@PathVariable Long staffSkillId, @RequestBody StaffSkill staffSkillDetails) {
        return staffSkillService.updateStaffSkill(staffSkillId, staffSkillDetails);
    }

    @DeleteMapping("/{staffSkillId}")
    public void deleteStaffSkill(@PathVariable Long staffSkillId) {
        staffSkillService.deleteStaffSkill(staffSkillId);
    }
}
