package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.StaffMerit;
import com.hcteol.jwt.backend.repositories.StaffMeritRepository;

@Service
public class StaffMeritService {

    @Autowired
    private StaffMeritRepository staffMeritRepository;

    public List<StaffMerit> getAllStaffMerits(String meritCategory) {
        if (meritCategory != null && !meritCategory.isBlank()) {
            return staffMeritRepository.findByMeritCategory(meritCategory);
        }
        return staffMeritRepository.findAll();
    }

    public Optional<StaffMerit> getStaffMeritById(Long id) {
        return staffMeritRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public StaffMerit createStaffMerit(StaffMerit staffMerit) {
        return staffMeritRepository.save(Objects.requireNonNull(staffMerit, "staffMerit cannot be null"));
    }

    public StaffMerit updateStaffMerit(Long id, StaffMerit staffMeritDetails) {
        Objects.requireNonNull(staffMeritDetails, "staffMeritDetails cannot be null");
        return staffMeritRepository.findById(Objects.requireNonNull(id, "id cannot be null")).map(staffMerit -> {
            staffMerit.setMeritName(staffMeritDetails.getMeritName());
            staffMerit.setMeritDescription(staffMeritDetails.getMeritDescription());
            staffMerit.setMeritCategory(staffMeritDetails.getMeritCategory());
            return staffMeritRepository.save(staffMerit);
        }).orElseThrow(() -> new RuntimeException("StaffMerit not found with id " + id));
    }

    public void deleteStaffMerit(Long id) {
        staffMeritRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
