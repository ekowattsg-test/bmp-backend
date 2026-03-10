package com.hcteol.jwt.backend.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.staffSkillProfile;

public interface StaffSkillProfileRepository extends JpaRepository<staffSkillProfile, Long> {
    List<staffSkillProfile> findByStaffName(String staffName);
    List<staffSkillProfile> findByStaffSkillId(Long staffSkillId);
}
