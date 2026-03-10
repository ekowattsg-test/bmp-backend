package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.StockMovement;
import com.hcteol.jwt.backend.repositories.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockMovementService {
    @Autowired
    private StockMovementRepository stockMovementRepository;

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public Optional<StockMovement> getStockMovementById(Long id) {
        return stockMovementRepository.findById(id);
    }

    public StockMovement createStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.save(stockMovement);
    }

    public StockMovement updateStockMovement(Long id, StockMovement stockMovementDetails) {
        return stockMovementRepository.findById(id).map(stockMovement -> {
            stockMovement.setStockId(stockMovementDetails.getStockId());
            stockMovement.setMovementType(stockMovementDetails.getMovementType());
            stockMovement.setQuantity(stockMovementDetails.getQuantity());
            stockMovement.setRecordDate(stockMovementDetails.getRecordDate());
            return stockMovementRepository.save(stockMovement);
        }).orElseThrow(() -> new RuntimeException("StockMovement not found with id " + id));
    }

    public void deleteStockMovement(Long id) {
        stockMovementRepository.deleteById(id);
    }
}
