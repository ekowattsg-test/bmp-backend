package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.StaffMeritProfile;

@Repository
public interface StaffMeritProfileRepository extends JpaRepository<StaffMeritProfile, Long> {

    List<StaffMeritProfile> findByStaffId(String staffId);

    List<StaffMeritProfile> findByStaffMeritId(Long staffMeritId);

    List<StaffMeritProfile> findByStaffIdAndStaffMeritId(String staffId, Long staffMeritId);
}
