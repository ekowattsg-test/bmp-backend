package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectUnitWork;

@Repository
public interface ProjectUnitWorkRepository extends JpaRepository<ProjectUnitWork, Long> {

    List<ProjectUnitWork> findByProjectUnitId(Long projectUnitId);

    List<ProjectUnitWork> findByProjectUnitIdOrderByProjectUnitWorkIdAsc(Long projectUnitId);

    List<ProjectUnitWork> findByProjectUnitIdAndProjectTaskId(Long projectUnitId, Long projectTaskId);

    List<ProjectUnitWork> findByProjectTaskId(Long projectTaskId);
}
