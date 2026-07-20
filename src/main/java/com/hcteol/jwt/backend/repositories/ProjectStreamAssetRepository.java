package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectStreamAsset;

@Repository
public interface ProjectStreamAssetRepository extends JpaRepository<ProjectStreamAsset, Long> {

    List<ProjectStreamAsset> findByProjectStreamId(Long projectStreamId);

    List<ProjectStreamAsset> findByRequisitionCycleId(Long requisitionCycleId);

    List<ProjectStreamAsset> findByProjectStreamIdAndRequisitionCycleId(Long projectStreamId, Long requisitionCycleId);
}
