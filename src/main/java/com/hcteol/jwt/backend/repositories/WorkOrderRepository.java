package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.WorkOrder;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

}
