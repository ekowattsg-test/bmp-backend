package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.StaffSkillProfileView;
import com.hcteol.jwt.backend.services.StaffSkillProfileViewService;

@RestController
@RequestMapping("/api/staffskillprofileviews")
public class StaffSkillProfileViewController {

    @Autowired
    private StaffSkillProfileViewService staffSkillProfileViewService;

    @GetMapping
    public List<StaffSkillProfileView> search(
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) Long staffSkillId,
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) String skillName) {

        if (staffId != null && !staffId.isBlank()) {
            return staffSkillProfileViewService.getByStaffId(staffId);
        }
        if (staffSkillId != null) {
            return staffSkillProfileViewService.getByStaffSkillId(staffSkillId);
        }
        if (staffName != null && !staffName.isBlank()) {
            return staffSkillProfileViewService.getByStaffName(staffName);
        }
        if (skillName != null && !skillName.isBlank()) {
            return staffSkillProfileViewService.getBySkillName(skillName);
        }

        return staffSkillProfileViewService.getAllStaffSkillProfileViews();
    }

    @GetMapping("/{rowId}")
    public ResponseEntity<StaffSkillProfileView> getById(@PathVariable Long rowId) {
        return staffSkillProfileViewService.getStaffSkillProfileViewById(rowId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/staff/{staffId}")
    public List<StaffSkillProfileView> getByStaffId(@PathVariable String staffId) {
        return staffSkillProfileViewService.getByStaffId(staffId);
    }

    @GetMapping("/skill/{staffSkillId}")
    public List<StaffSkillProfileView> getByStaffSkillId(@PathVariable Long staffSkillId) {
        return staffSkillProfileViewService.getByStaffSkillId(staffSkillId);
    }
}
