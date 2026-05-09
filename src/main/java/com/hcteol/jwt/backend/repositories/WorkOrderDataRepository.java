package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.WorkOrderData;

public interface WorkOrderDataRepository extends JpaRepository<WorkOrderData, Long> {

}
