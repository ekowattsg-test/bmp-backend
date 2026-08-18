package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.StockDisposal;
import com.hcteol.jwt.backend.repositories.StockDisposalRepository;

@RestController
@RequestMapping("/api/stockDisposals")
public class StockDisposalController {

    @Autowired
    private StockDisposalRepository stockDisposalRepository;

    @GetMapping
    public List<StockDisposal> getAllStockDisposals() {
        return stockDisposalRepository.findAll();
    }

    @GetMapping("/{disposalId}")
    public ResponseEntity<StockDisposal> getStockDisposalById(@PathVariable Long disposalId) {
        return stockDisposalRepository.findById(disposalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StockDisposal createStockDisposal(@RequestBody StockDisposal stockDisposal) {
        return stockDisposalRepository.save(stockDisposal);
    }

    @PutMapping("/{disposalId}")
    public ResponseEntity<StockDisposal> updateStockDisposal(
            @PathVariable Long disposalId,
            @RequestBody StockDisposal stockDisposal) {
        return stockDisposalRepository.findById(disposalId)
                .map(existing -> {
                    if (stockDisposal.getLocation() != null) {
                        existing.setLocation(stockDisposal.getLocation());
                    }
                    if (stockDisposal.getDisposedBy() != null) {
                        existing.setDisposedBy(stockDisposal.getDisposedBy());
                    }
                    if (stockDisposal.getDisposalDate() != null) {
                        existing.setDisposalDate(stockDisposal.getDisposalDate());
                    }
                    if (stockDisposal.getDisposalReason() != null) {
                        existing.setDisposalReason(stockDisposal.getDisposalReason());
                    }
                    if (stockDisposal.getDisposalMethod() != null) {
                        existing.setDisposalMethod(stockDisposal.getDisposalMethod());
                    }
                    if (stockDisposal.getDisposalStatus() != null) {
                        existing.setDisposalStatus(stockDisposal.getDisposalStatus());
                    }
                    if (stockDisposal.getTotalQuantity() != null) {
                        existing.setTotalQuantity(stockDisposal.getTotalQuantity());
                    }
                    return ResponseEntity.ok(stockDisposalRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{disposalId}")
    public ResponseEntity<Void> deleteStockDisposal(@PathVariable Long disposalId) {
        if (!stockDisposalRepository.existsById(disposalId)) {
            return ResponseEntity.notFound().build();
        }
        stockDisposalRepository.deleteById(disposalId);
        return ResponseEntity.noContent().build();
    }
}
