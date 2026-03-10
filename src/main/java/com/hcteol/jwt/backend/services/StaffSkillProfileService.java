package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.staffSkillProfile;
import com.hcteol.jwt.backend.repositories.StaffSkillProfileRepository;

@Service
public class StaffSkillProfileService {

    @Autowired
    private StaffSkillProfileRepository staffSkillProfileRepository;

    public staffSkillProfile addStaffSkillProfile(staffSkillProfile staffSkillProfile) {
        return staffSkillProfileRepository.save(staffSkillProfile);
    }

    public List<staffSkillProfile> getAllStaffSkillProfiles() {
        return staffSkillProfileRepository.findAll();
    }

    public staffSkillProfile getStaffSkillProfileById(Long staffSkillProfileId) {
        return staffSkillProfileRepository.findById(staffSkillProfileId).orElse(null);
    }

    public staffSkillProfile updateStaffSkillProfile(Long staffSkillProfileId, staffSkillProfile staffSkillProfileDetails) {
        staffSkillProfile existingProfile = staffSkillProfileRepository.findById(staffSkillProfileId).orElse(null);
        if (existingProfile != null) {
            existingProfile.setStaffName(staffSkillProfileDetails.getStaffName());
            existingProfile.setStaffSkillId(staffSkillProfileDetails.getStaffSkillId());
            existingProfile.setIssuedBy(staffSkillProfileDetails.getIssuedBy());
            existingProfile.setAcquiredDate(staffSkillProfileDetails.getAcquiredDate());
            existingProfile.setExpiryDate(staffSkillProfileDetails.getExpiryDate());
            existingProfile.setNoExpiry(staffSkillProfileDetails.getNoExpiry());
            existingProfile.setCertificationLink(staffSkillProfileDetails.getCertificationLink());
            return staffSkillProfileRepository.save(existingProfile);
        }
        return null;
    }

    public void deleteStaffSkillProfile(Long staffSkillProfileId) {
        staffSkillProfileRepository.deleteById(staffSkillProfileId);
    }

    public List<staffSkillProfile> getStaffSkillProfilesByStaffName(String staffName) {
        return staffSkillProfileRepository.findByStaffName(staffName);
    }

    public List<staffSkillProfile> getStaffSkillProfilesByStaffSkillId(Long staffSkillId) {
        return staffSkillProfileRepository.findByStaffSkillId(staffSkillId);
    }
}
