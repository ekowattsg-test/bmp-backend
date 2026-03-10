package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.StaffSkill;
import com.hcteol.jwt.backend.repositories.StaffSkillRepository;

@Service
public class StaffSkillService {

    @Autowired
    private StaffSkillRepository staffSkillRepository;

    public StaffSkill addStaffSkill(StaffSkill staffSkill) {
        return staffSkillRepository.save(staffSkill);
    }

    public List<StaffSkill> getAllStaffSkills() {
        return staffSkillRepository.findAll();
    }

    public StaffSkill getStaffSkillById(Long staffSkillId) {
        return staffSkillRepository.findById(staffSkillId).orElse(null);
    }

    public StaffSkill updateStaffSkill(Long staffSkillId, StaffSkill staffSkillDetails) {
        StaffSkill existingStaffSkill = staffSkillRepository.findById(staffSkillId).orElse(null);
        if (existingStaffSkill != null) {
            existingStaffSkill.setSkillName(staffSkillDetails.getSkillName());
            existingStaffSkill.setSkillDescription(staffSkillDetails.getSkillDescription());
            existingStaffSkill.setSkillCategory(staffSkillDetails.getSkillCategory());
            return staffSkillRepository.save(existingStaffSkill);
        }
        return null;
    }

    public void deleteStaffSkill(Long staffSkillId) {
        staffSkillRepository.deleteById(staffSkillId);
    }
}
