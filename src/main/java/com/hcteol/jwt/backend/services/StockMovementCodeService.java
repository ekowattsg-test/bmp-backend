package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.StockMovementCode;
import com.hcteol.jwt.backend.repositories.StockMovementCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockMovementCodeService {
    @Autowired
    private StockMovementCodeRepository stockMovementCodeRepository;

    public List<StockMovementCode> getAllStockMovementCodes() {
        return stockMovementCodeRepository.findAll();
    }

    public Optional<StockMovementCode> getStockMovementCodeById(String movementType) {
        return stockMovementCodeRepository.findById(movementType);
    }

    public StockMovementCode createStockMovementCode(StockMovementCode code) {
        return stockMovementCodeRepository.save(code);
    }

    public StockMovementCode updateStockMovementCode(String movementType, StockMovementCode codeDetails) {
        return stockMovementCodeRepository.findById(movementType).map(code -> {
            code.setMovementDescription(codeDetails.getMovementDescription());
            code.setStockModifier(codeDetails.getStockModifier());
            code.setHoldModifier(codeDetails.getHoldModifier());
            return stockMovementCodeRepository.save(code);
        }).orElseThrow(() -> new RuntimeException("StockMovementCode not found with movementType " + movementType));
    }

    public void deleteStockMovementCode(String movementType) {
        stockMovementCodeRepository.deleteById(movementType);
    }
}
