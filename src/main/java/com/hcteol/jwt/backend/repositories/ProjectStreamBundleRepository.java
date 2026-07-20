package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectStreamBundle;

@Repository
public interface ProjectStreamBundleRepository extends JpaRepository<ProjectStreamBundle, Long> {

    List<ProjectStreamBundle> findByProjectStreamId(Long projectStreamId);

    List<ProjectStreamBundle> findByRequisitionCycleId(Long requisitionCycleId);

    List<ProjectStreamBundle> findByProjectStreamIdAndRequisitionCycleId(Long projectStreamId, Long requisitionCycleId);
}
