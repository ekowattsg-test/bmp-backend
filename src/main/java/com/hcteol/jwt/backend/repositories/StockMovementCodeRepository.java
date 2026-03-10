package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.StockMovementCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementCodeRepository extends JpaRepository<StockMovementCode, String> {
}
