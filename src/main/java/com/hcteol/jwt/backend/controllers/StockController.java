// ...existing code...
package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.Stock;
import com.hcteol.jwt.backend.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
        @GetMapping("/search")
        public ResponseEntity<Stock> getStockByStockCode(@RequestParam String stockCode) {
            Stock stock = stockService.getStockByStockCode(stockCode);
            if (stock != null) {
                return ResponseEntity.ok(stock);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    @Autowired
    private StockService stockService;

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        return stockService.getStockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Stock createStock(@RequestBody Stock stock) {
        return stockService.createStock(stock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        try {
            Stock updatedStock = stockService.updateStock(id, stock);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
