package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hcteol.jwt.backend.entities.ProjectManpowerView;

public interface ProjectManpowerViewRepository extends JpaRepository<ProjectManpowerView, Long>, JpaSpecificationExecutor<ProjectManpowerView> {

    List<ProjectManpowerView> findByProjectCode(String projectCode);

    List<ProjectManpowerView> findByProjectTaskId(Long projectTaskId);

    List<ProjectManpowerView> findByProjectSkillId(Long projectSkillId);

    List<ProjectManpowerView> findByStaffId(String staffId);
}
