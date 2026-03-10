package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.StockMovement;
import com.hcteol.jwt.backend.services.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockmovements")
public class StockMovementController {
    @Autowired
    private StockMovementService stockMovementService;

    @GetMapping
    public List<StockMovement> getAllStockMovements() {
        return stockMovementService.getAllStockMovements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getStockMovementById(@PathVariable Long id) {
        return stockMovementService.getStockMovementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StockMovement createStockMovement(@RequestBody StockMovement stockMovement) {
        return stockMovementService.createStockMovement(stockMovement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockMovement> updateStockMovement(@PathVariable Long id, @RequestBody StockMovement stockMovement) {
        try {
            StockMovement updatedStockMovement = stockMovementService.updateStockMovement(id, stockMovement);
            return ResponseEntity.ok(updatedStockMovement);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
        stockMovementService.deleteStockMovement(id);
        return ResponseEntity.noContent().build();
    }
}
