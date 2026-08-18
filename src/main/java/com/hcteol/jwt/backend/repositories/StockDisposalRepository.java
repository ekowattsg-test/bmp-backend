package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.StockDisposal;

@Repository
public interface StockDisposalRepository extends JpaRepository<StockDisposal, Long> {
}
