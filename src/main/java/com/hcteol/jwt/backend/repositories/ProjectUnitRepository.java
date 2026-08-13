package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectUnit;

@Repository
public interface ProjectUnitRepository extends JpaRepository<ProjectUnit, Long> {

    List<ProjectUnit> findByProjectStoreyId(Long projectStoreyId);

    List<ProjectUnit> findByProjectStoreyIdOrderByUnitNumberAsc(Long projectStoreyId);

    List<ProjectUnit> findByProjectStoreyIdAndStatusOrderByUnitNumberAsc(Long projectStoreyId, String status);

    List<ProjectUnit> findByProjectStackIdOrderByUnitNumberAsc(Long projectStackId);

    List<ProjectUnit> findByProjectStackIdAndStatusOrderByUnitNumberAsc(Long projectStackId, String status);

    List<ProjectUnit> findByProjectStoreyIdAndProjectStackIdOrderByUnitNumberAsc(Long projectStoreyId,
            Long projectStackId);

    List<ProjectUnit> findByProjectStoreyIdAndProjectStackId(Long projectStoreyId, Long projectStackId);

    List<ProjectUnit> findByProjectStoreyIdAndUnitNumber(Long projectStoreyId, Long unitNumber);

    List<ProjectUnit> findByProjectStackId(Long projectStackId);

    List<ProjectUnit> findByProjectStreamId(Long projectStreamId);
}
