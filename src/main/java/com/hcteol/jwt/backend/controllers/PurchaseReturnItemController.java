package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.PurchaseReturnItem;
import com.hcteol.jwt.backend.repositories.PurchaseReturnItemRepository;

@RestController
@RequestMapping("/api/purchaseReturnItems")
public class PurchaseReturnItemController {

    @Autowired
    private PurchaseReturnItemRepository purchaseReturnItemRepository;

    @PostMapping
    public PurchaseReturnItem createPurchaseReturnItem(@RequestBody PurchaseReturnItem item) {
        return purchaseReturnItemRepository.save(item);
    }

    @GetMapping("/return/{returnId}")
    public List<PurchaseReturnItem> getItemsByReturnId(@PathVariable Long returnId) {
        return purchaseReturnItemRepository.findByReturnId(returnId);
    }
}
