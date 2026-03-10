package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.PurchaseOrderDto;
import com.hcteol.jwt.backend.services.PurchaseOrderService;

@RestController
@RequestMapping("/api/purchaseOrders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * Create a new purchase order
     */
    @PostMapping
    public ResponseEntity<PurchaseOrderDto> createPurchaseOrder(@RequestBody PurchaseOrderDto purchaseOrderDto) {
        try {
            PurchaseOrderDto createdOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all purchase orders
     */
    @GetMapping
    public ResponseEntity<List<PurchaseOrderDto>> getAllPurchaseOrders() {
        List<PurchaseOrderDto> orders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get a purchase order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderById(@PathVariable String orderId) {
        try {
            PurchaseOrderDto order = purchaseOrderService.getPurchaseOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get purchase orders by vendor ID
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrdersByVendorId(@PathVariable Long vendorId) {
        List<PurchaseOrderDto> orders = purchaseOrderService.getPurchaseOrdersByVendorId(vendorId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get purchase orders by status
     */
    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrdersByStatus(@PathVariable String orderStatus) {
        List<PurchaseOrderDto> orders = purchaseOrderService.getPurchaseOrdersByStatus(orderStatus);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update a purchase order
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDto> updatePurchaseOrder(
            @PathVariable String orderId, 
            @RequestBody PurchaseOrderDto purchaseOrderDto) {
        try {
            PurchaseOrderDto updatedOrder = purchaseOrderService.updatePurchaseOrder(orderId, purchaseOrderDto);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Update order status only
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<PurchaseOrderDto> updateOrderStatus(
            @PathVariable String orderId, 
            @RequestParam String status) {
        try {
            PurchaseOrderDto updatedOrder = purchaseOrderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete a purchase order
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable String orderId) {
        try {
            purchaseOrderService.deletePurchaseOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
