package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.PurchaseReturn;
import com.hcteol.jwt.backend.repositories.PurchaseReturnRepository;

@RestController
@RequestMapping("/api/purchaseReturns")
public class PurchaseReturnController {

    @Autowired
    private PurchaseReturnRepository purchaseReturnRepository;

    @PostMapping
    public PurchaseReturn createPurchaseReturn(@RequestBody PurchaseReturn purchaseReturn) {
        return purchaseReturnRepository.save(purchaseReturn);
    }

    @GetMapping
    public List<PurchaseReturn> getAllPurchaseReturns() {
        return purchaseReturnRepository.findAll();
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<PurchaseReturn> getPurchaseReturnById(@PathVariable Long returnId) {
        return purchaseReturnRepository.findById(returnId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/po/{poId}")
    public List<PurchaseReturn> getPurchaseReturnsByPoId(@PathVariable String poId) {
        return purchaseReturnRepository.findByPoId(poId);
    }

    @PutMapping("/{returnId}")
    public ResponseEntity<PurchaseReturn> updatePurchaseReturn(
            @PathVariable Long returnId,
            @RequestBody PurchaseReturn purchaseReturn) {
        return purchaseReturnRepository.findById(returnId)
                .map(existing -> {
                    if (purchaseReturn.getReturnStatus() != null) {
                        existing.setReturnStatus(purchaseReturn.getReturnStatus());
                    }
                    if (purchaseReturn.getTotalQuantity() != null) {
                        existing.setTotalQuantity(purchaseReturn.getTotalQuantity());
                    }
                    if (purchaseReturn.getCreditAmount() != null) {
                        existing.setCreditAmount(purchaseReturn.getCreditAmount());
                    }
                    return ResponseEntity.ok(purchaseReturnRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
