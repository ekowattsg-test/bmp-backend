package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByStockCode(String stockCode);

    Stock findByProductIdAndStockCode(Long productId, String stockCode);
}
