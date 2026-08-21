package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.TaskDeliveryRequirement;

@Repository
public interface TaskDeliveryRequirementRepository extends JpaRepository<TaskDeliveryRequirement, Long> {

    List<TaskDeliveryRequirement> findByWeekStartDate(String weekStartDate);

    List<TaskDeliveryRequirement> findByProjectCode(String projectCode);

    List<TaskDeliveryRequirement> findByProjectTaskId(Long projectTaskId);

    List<TaskDeliveryRequirement> findByProductId(Long productId);

    List<TaskDeliveryRequirement> findByStatus(String status);

    List<TaskDeliveryRequirement> findByDeliveryOrderId(String deliveryOrderId);

    Optional<TaskDeliveryRequirement> findByWeekStartDateAndActivityIdAndInventoryTypeAndProductId(
            String weekStartDate,
            Long activityId,
            String inventoryType,
            Long productId);

    List<TaskDeliveryRequirement> findByWeekStartDateAndProjectCode(String weekStartDate, String projectCode);
}
