package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.StockMovementCode;
import com.hcteol.jwt.backend.services.StockMovementCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockmovementcodes")
public class StockMovementCodeController {
    @Autowired
    private StockMovementCodeService stockMovementCodeService;

    @GetMapping
    public List<StockMovementCode> getAllStockMovementCodes() {
        return stockMovementCodeService.getAllStockMovementCodes();
    }

    @GetMapping("/{movementType}")
    public ResponseEntity<StockMovementCode> getStockMovementCodeById(@PathVariable String movementType) {
        return stockMovementCodeService.getStockMovementCodeById(movementType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StockMovementCode createStockMovementCode(@RequestBody StockMovementCode code) {
        return stockMovementCodeService.createStockMovementCode(code);
    }

    @PutMapping("/{movementType}")
    public ResponseEntity<StockMovementCode> updateStockMovementCode(@PathVariable String movementType, @RequestBody StockMovementCode code) {
        try {
            StockMovementCode updatedCode = stockMovementCodeService.updateStockMovementCode(movementType, code);
            return ResponseEntity.ok(updatedCode);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{movementType}")
    public ResponseEntity<Void> deleteStockMovementCode(@PathVariable String movementType) {
        stockMovementCodeService.deleteStockMovementCode(movementType);
        return ResponseEntity.noContent().build();
    }
}
