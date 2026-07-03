package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectManpower;

@Repository
public interface ProjectManpowerRepository extends JpaRepository<ProjectManpower, Long> {

    long deleteByWorkDateIsNull();

    List<ProjectManpower> findByProjectSkillId(Long projectSkillId);

    List<ProjectManpower> findByProjectSkillIdIn(List<Long> projectSkillIds);

    List<ProjectManpower> findByProjectTaskIdIn(List<Long> projectTaskIds);

    @Query("select projectManpower from ProjectManpower projectManpower "
            + "where projectManpower.projectSkillId in ("
            + "select projectSkill.projectSkillId from ProjectSkill projectSkill where projectSkill.projectTaskId = :projectTaskId)")
    List<ProjectManpower> findByProjectTaskId(@Param("projectTaskId") Long projectTaskId);

    List<ProjectManpower> findByStaffId(String staffId);
}
