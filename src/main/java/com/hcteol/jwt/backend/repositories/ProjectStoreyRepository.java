package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectStorey;

@Repository
public interface ProjectStoreyRepository extends JpaRepository<ProjectStorey, Long> {

    List<ProjectStorey> findByProjectBlockId(Long projectBlockId);

    List<ProjectStorey> findByProjectBlockIdOrderByStoreyNumberAsc(Long projectBlockId);

    List<ProjectStorey> findByProjectBlockIdAndStatusOrderByStoreyNumberAsc(Long projectBlockId, String status);

    List<ProjectStorey> findByProjectBlockIdAndStoreyNumber(Long projectBlockId, Long storeyNumber);
}
