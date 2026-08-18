package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.StockDisposalItem;
import com.hcteol.jwt.backend.repositories.StockDisposalItemRepository;

@RestController
@RequestMapping("/api/stockDisposalItems")
public class StockDisposalItemController {

    @Autowired
    private StockDisposalItemRepository stockDisposalItemRepository;

    @PostMapping
    public StockDisposalItem createStockDisposalItem(@RequestBody StockDisposalItem item) {
        return stockDisposalItemRepository.save(item);
    }

    @GetMapping("/disposal/{disposalId}")
    public List<StockDisposalItem> getItemsByDisposalId(@PathVariable Long disposalId) {
        return stockDisposalItemRepository.findByDisposalId(disposalId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteStockDisposalItem(@PathVariable Long itemId) {
        if (!stockDisposalItemRepository.existsById(itemId)) {
            return ResponseEntity.notFound().build();
        }
        stockDisposalItemRepository.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }
}
