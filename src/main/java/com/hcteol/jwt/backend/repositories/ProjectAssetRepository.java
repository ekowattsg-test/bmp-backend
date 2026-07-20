package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectAsset;

@Repository
public interface ProjectAssetRepository extends JpaRepository<ProjectAsset, Long> {

    List<ProjectAsset> findByProjectTaskId(Long projectTaskId);

    List<ProjectAsset> findByRequisitionCycleId(Long requisitionCycleId);

    List<ProjectAsset> findByProjectTaskIdAndRequisitionCycleId(Long projectTaskId, Long requisitionCycleId);
}
