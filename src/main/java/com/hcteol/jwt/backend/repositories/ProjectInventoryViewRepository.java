package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hcteol.jwt.backend.entities.ProjectInventoryView;

public interface ProjectInventoryViewRepository extends JpaRepository<ProjectInventoryView, String>, JpaSpecificationExecutor<ProjectInventoryView> {

    List<ProjectInventoryView> findByProjectCode(String projectCode);

    List<ProjectInventoryView> findByInventoryType(String inventoryType);

    List<ProjectInventoryView> findByProductId(Long productId);

    List<ProjectInventoryView> findByActivityId(Long activityId);

    List<ProjectInventoryView> findByRequisitionCycleId(Long requisitionCycleId);

    List<ProjectInventoryView> findByProjectCodeAndInventoryType(String projectCode, String inventoryType);
}
