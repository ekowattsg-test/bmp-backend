package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.PurchaseOrderView;
import com.hcteol.jwt.backend.repositories.PurchaseOrderViewRepository;

@RestController
@RequestMapping("/api/purchaseorderview")
public class PurchaseOrderViewController {

    @Autowired
    private PurchaseOrderViewRepository purchaseOrderViewRepository;

    // GET all purchase order view records
    @GetMapping
    public ResponseEntity<List<PurchaseOrderView>> getAll() {
        return ResponseEntity.ok(purchaseOrderViewRepository.findAll());
    }

    // GET by item_id (PK)
    @GetMapping("/{itemId}")
    public ResponseEntity<PurchaseOrderView> getByItemId(@PathVariable String itemId) {
        return purchaseOrderViewRepository.findById(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET all items for a given order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PurchaseOrderView>> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(purchaseOrderViewRepository.findByOrderId(orderId));
    }

    // GET all orders for a given vendor
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<PurchaseOrderView>> getByVendorId(@PathVariable Long vendorId) {
        return ResponseEntity.ok(purchaseOrderViewRepository.findByVendorId(vendorId));
    }

    // GET all orders by status
    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<List<PurchaseOrderView>> getByOrderStatus(@PathVariable String orderStatus) {
        return ResponseEntity.ok(purchaseOrderViewRepository.findByOrderStatus(orderStatus));
    }

    // GET all order items for a given product code
    @GetMapping("/product/{productCode}")
    public ResponseEntity<List<PurchaseOrderView>> getByProductCode(@PathVariable String productCode) {
        return ResponseEntity.ok(purchaseOrderViewRepository.findByProductCode(productCode));
    }
}
