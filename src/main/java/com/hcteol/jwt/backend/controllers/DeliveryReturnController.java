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

import com.hcteol.jwt.backend.entities.DeliveryReturn;
import com.hcteol.jwt.backend.repositories.DeliveryReturnRepository;

@RestController
@RequestMapping("/api/deliveryReturns")
public class DeliveryReturnController {

    @Autowired
    private DeliveryReturnRepository deliveryReturnRepository;

    @PostMapping
    public DeliveryReturn createDeliveryReturn(@RequestBody DeliveryReturn deliveryReturn) {
        return deliveryReturnRepository.save(deliveryReturn);
    }

    @GetMapping
    public List<DeliveryReturn> getAllDeliveryReturns() {
        return deliveryReturnRepository.findAll();
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<DeliveryReturn> getDeliveryReturnById(@PathVariable Long returnId) {
        return deliveryReturnRepository.findById(returnId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/do/{doId}")
    public List<DeliveryReturn> getDeliveryReturnsByDoId(@PathVariable String doId) {
        return deliveryReturnRepository.findByDoId(doId);
    }

    @PutMapping("/{returnId}")
    public ResponseEntity<DeliveryReturn> updateDeliveryReturn(
            @PathVariable Long returnId,
            @RequestBody DeliveryReturn deliveryReturn) {
        return deliveryReturnRepository.findById(returnId)
                .map(existing -> {
                    if (deliveryReturn.getReturnStatus() != null) {
                        existing.setReturnStatus(deliveryReturn.getReturnStatus());
                    }
                    if (deliveryReturn.getTotalQuantity() != null) {
                        existing.setTotalQuantity(deliveryReturn.getTotalQuantity());
                    }
                    return ResponseEntity.ok(deliveryReturnRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
