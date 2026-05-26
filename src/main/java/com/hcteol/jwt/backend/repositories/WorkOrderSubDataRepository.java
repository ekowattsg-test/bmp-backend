package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.WorkOrderSubData;

public interface WorkOrderSubDataRepository extends JpaRepository<WorkOrderSubData, Long> {

    List<WorkOrderSubData> findByWorkOrderDataId(Long workOrderDataId);

}
