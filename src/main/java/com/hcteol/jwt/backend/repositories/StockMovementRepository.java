package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.StockMovement;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    java.util.List<StockMovement> findByStockIdAndReference(Long stockId, String reference);

    java.util.List<StockMovement> findByStockIdAndReferenceAndWorkOrderId(Long stockId, String reference, String workOrderId);

    java.util.List<StockMovement> findByWorkOrderId(String workOrderId);

    java.util.List<StockMovement> findByReferenceAndMovementType(String reference, String movementType);
}
