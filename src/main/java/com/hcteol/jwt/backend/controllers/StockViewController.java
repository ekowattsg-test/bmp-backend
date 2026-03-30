package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcteol.jwt.backend.entities.StockView;
import com.hcteol.jwt.backend.repositories.StockViewRepository;

@RestController
@RequestMapping("/api/stockviews")
public class StockViewController {

    private static final Logger log = LoggerFactory.getLogger(StockViewController.class);

    @Autowired
    private StockViewRepository stockViewRepository;

    // GET all stock view records
    @GetMapping
    public ResponseEntity<List<StockView>> getAllStockViews() {
        return ResponseEntity.ok(stockViewRepository.findAll());
    }

    // GET by movement_id (the PK of the view)
    @GetMapping("/{movementId}")
    public ResponseEntity<StockView> getStockViewByMovementId(@PathVariable Long movementId) {
        return stockViewRepository.findById(movementId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET all movements for a given stock
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<List<StockView>> getByStockId(@PathVariable Long stockId) {
        return ResponseEntity.ok(stockViewRepository.findByStockId(stockId));
    }

    // GET all movements for a given product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockView>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(stockViewRepository.findByProductId(productId));
    }

    // GET all movements for a given stock code
    @GetMapping("/stockcode/{stockCode}")
    public ResponseEntity<List<StockView>> getByStockCode(@PathVariable String stockCode) {
        log.debug("getByStockCode called with stockCode: '{}'", stockCode);
        List<StockView> results = stockViewRepository.findByStockCode(stockCode);
        log.debug("getByStockCode returning {} record(s): {}", results.size(), results);
        return ResponseEntity.ok(results);
    }
}
