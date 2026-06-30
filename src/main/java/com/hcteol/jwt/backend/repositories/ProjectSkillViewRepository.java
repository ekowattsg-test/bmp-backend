package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hcteol.jwt.backend.entities.ProjectSkillView;

public interface ProjectSkillViewRepository extends JpaRepository<ProjectSkillView, Long>, JpaSpecificationExecutor<ProjectSkillView> {

    List<ProjectSkillView> findByProjectCode(String projectCode);

    List<ProjectSkillView> findByProjectTaskId(Long projectTaskId);

    List<ProjectSkillView> findByStaffSkillId(Long staffSkillId);
}
