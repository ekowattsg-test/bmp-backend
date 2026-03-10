package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.PurchaseOrderItemDto;
import com.hcteol.jwt.backend.services.PurchaseOrderItemService;

@RestController
@RequestMapping("/api/purchaseOrderItems")
public class PurchaseOrderItemController {

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    /**
     * Create a new purchase order item
     */
    @PostMapping
    public ResponseEntity<PurchaseOrderItemDto> createPurchaseOrderItem(@RequestBody PurchaseOrderItemDto itemDto) {
        try {
            PurchaseOrderItemDto createdItem = purchaseOrderItemService.createPurchaseOrderItem(itemDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all purchase order items
     */
    @GetMapping
    public ResponseEntity<List<PurchaseOrderItemDto>> getAllPurchaseOrderItems() {
        List<PurchaseOrderItemDto> items = purchaseOrderItemService.getAllPurchaseOrderItems();
        return ResponseEntity.ok(items);
    }

    /**
     * Get a purchase order item by ID
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<PurchaseOrderItemDto> getPurchaseOrderItemById(@PathVariable String itemId) {
        try {
            PurchaseOrderItemDto item = purchaseOrderItemService.getPurchaseOrderItemById(itemId);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get all items for a specific purchase order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PurchaseOrderItemDto>> getPurchaseOrderItemsByOrderId(@PathVariable String orderId) {
        List<PurchaseOrderItemDto> items = purchaseOrderItemService.getPurchaseOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(items);
    }

    /**
     * Update a purchase order item
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<PurchaseOrderItemDto> updatePurchaseOrderItem(
            @PathVariable String itemId,
            @RequestBody PurchaseOrderItemDto itemDto) {
        try {
            PurchaseOrderItemDto updatedItem = purchaseOrderItemService.updatePurchaseOrderItem(itemId, itemDto);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete a purchase order item
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deletePurchaseOrderItem(@PathVariable String itemId) {
        try {
            purchaseOrderItemService.deletePurchaseOrderItem(itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete all items for a specific purchase order
     */
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> deletePurchaseOrderItemsByOrderId(@PathVariable String orderId) {
        purchaseOrderItemService.deletePurchaseOrderItemsByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
