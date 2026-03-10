package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
	Stock findByStockCode(String stockCode);
}
