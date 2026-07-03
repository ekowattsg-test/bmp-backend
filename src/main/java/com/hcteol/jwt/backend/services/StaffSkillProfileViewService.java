package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.StaffSkillProfileView;
import com.hcteol.jwt.backend.repositories.StaffSkillProfileViewRepository;

@Service
public class StaffSkillProfileViewService {

    @Autowired
    private StaffSkillProfileViewRepository staffSkillProfileViewRepository;

    public List<StaffSkillProfileView> getAllStaffSkillProfileViews() {
        return staffSkillProfileViewRepository.findAll();
    }

    public Optional<StaffSkillProfileView> getStaffSkillProfileViewById(Long rowId) {
        if (rowId == null) {
            return Optional.empty();
        }
        return staffSkillProfileViewRepository.findById(rowId);
    }

    public List<StaffSkillProfileView> getByStaffId(String staffId) {
        return staffSkillProfileViewRepository.findByStaffId(staffId);
    }

    public List<StaffSkillProfileView> getByStaffSkillId(Long staffSkillId) {
        return staffSkillProfileViewRepository.findByStaffSkillId(staffSkillId);
    }

    public List<StaffSkillProfileView> getByStaffName(String staffName) {
        return staffSkillProfileViewRepository.findByStaffNameContainingIgnoreCase(staffName);
    }

    public List<StaffSkillProfileView> getBySkillName(String skillName) {
        return staffSkillProfileViewRepository.findBySkillNameContainingIgnoreCase(skillName);
    }
}
