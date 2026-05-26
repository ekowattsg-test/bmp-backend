package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.WorkOrderData;

public interface WorkOrderDataRepository extends JpaRepository<WorkOrderData, Long> {

    List<WorkOrderData> findByWorkOrderId(String workOrderId);

}
