package com.hcteol.jwt.backend.repositories;

import java.util.List;

import com.hcteol.jwt.backend.entities.StockView;

public interface StockViewRepositoryCustom {

    List<StockView> findByStockId(Long stockId);

    List<StockView> findByProductId(Long productId);

    List<StockView> findByStockCode(String stockCode);
}
