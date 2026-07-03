package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.StaffSkillProfileView;

public interface StaffSkillProfileViewRepository extends JpaRepository<StaffSkillProfileView, Long> {

    List<StaffSkillProfileView> findByStaffId(String staffId);

    List<StaffSkillProfileView> findByStaffSkillId(Long staffSkillId);

    List<StaffSkillProfileView> findByStaffNameContainingIgnoreCase(String staffName);

    List<StaffSkillProfileView> findBySkillNameContainingIgnoreCase(String skillName);
}
