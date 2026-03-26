package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.StockView;

public interface StockViewRepository extends JpaRepository<StockView, Long>, StockViewRepositoryCustom {

    List<StockView> findByProductId(Long productId);

    List<StockView> findByStockId(Long stockId);

    List<StockView> findByStockCode(String stockCode);
}
