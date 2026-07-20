package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.RequisitionCycle;

@Repository
public interface RequisitionCycleRepository extends JpaRepository<RequisitionCycle, Long> {

    List<RequisitionCycle> findByStatus(String status);

    Optional<RequisitionCycle> findByRequisitionCycleDate(String requisitionCycleDate);
}
