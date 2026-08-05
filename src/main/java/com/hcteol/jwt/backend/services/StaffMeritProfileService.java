package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.StaffMeritProfile;
import com.hcteol.jwt.backend.repositories.StaffMeritProfileRepository;

@Service
public class StaffMeritProfileService {

    @Autowired
    private StaffMeritProfileRepository staffMeritProfileRepository;

    public List<StaffMeritProfile> getAllStaffMeritProfiles(String staffId, Long staffMeritId) {
        if (staffId != null && !staffId.isBlank() && staffMeritId != null) {
            return staffMeritProfileRepository.findByStaffIdAndStaffMeritId(staffId, staffMeritId);
        }
        if (staffId != null && !staffId.isBlank()) {
            return staffMeritProfileRepository.findByStaffId(staffId);
        }
        if (staffMeritId != null) {
            return staffMeritProfileRepository.findByStaffMeritId(staffMeritId);
        }
        return staffMeritProfileRepository.findAll();
    }

    public Optional<StaffMeritProfile> getStaffMeritProfileById(Long id) {
        return staffMeritProfileRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public StaffMeritProfile createStaffMeritProfile(StaffMeritProfile staffMeritProfile) {
        return staffMeritProfileRepository
                .save(Objects.requireNonNull(staffMeritProfile, "staffMeritProfile cannot be null"));
    }

    public StaffMeritProfile updateStaffMeritProfile(Long id, StaffMeritProfile staffMeritProfileDetails) {
        Objects.requireNonNull(staffMeritProfileDetails, "staffMeritProfileDetails cannot be null");
        return staffMeritProfileRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .map(staffMeritProfile -> {
                    staffMeritProfile.setStaffId(staffMeritProfileDetails.getStaffId());
                    staffMeritProfile.setStaffMeritId(staffMeritProfileDetails.getStaffMeritId());
                    staffMeritProfile.setIssuedBy(staffMeritProfileDetails.getIssuedBy());
                    staffMeritProfile.setIssuedDate(staffMeritProfileDetails.getIssuedDate());
                    staffMeritProfile.setMeritPoints(staffMeritProfileDetails.getMeritPoints());
                    staffMeritProfile.setMeritRemarks(staffMeritProfileDetails.getMeritRemarks());
                    staffMeritProfile.setDocumentationLink(staffMeritProfileDetails.getDocumentationLink());
                    return staffMeritProfileRepository.save(staffMeritProfile);
                }).orElseThrow(() -> new RuntimeException("StaffMeritProfile not found with id " + id));
    }

    public void deleteStaffMeritProfile(Long id) {
        staffMeritProfileRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
