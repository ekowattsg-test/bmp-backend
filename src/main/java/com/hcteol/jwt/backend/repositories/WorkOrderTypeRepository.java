package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.WorkOrderType;

public interface WorkOrderTypeRepository extends JpaRepository<WorkOrderType, String> {

}
