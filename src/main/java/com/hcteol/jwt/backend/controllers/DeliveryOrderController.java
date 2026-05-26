package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.dtos.DeliveryOrderDto;
import com.hcteol.jwt.backend.services.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveryOrders")
public class DeliveryOrderController {

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @GetMapping
    public List<DeliveryOrderDto> getAllDeliveryOrders() {
        return deliveryOrderService.getAllDeliveryOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<DeliveryOrderDto> getDeliveryOrderById(@PathVariable String orderId) {
        DeliveryOrderDto order = deliveryOrderService.getDeliveryOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<DeliveryOrderDto> createDeliveryOrder(@RequestBody DeliveryOrderDto dto) {
        DeliveryOrderDto created = deliveryOrderService.createDeliveryOrder(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<DeliveryOrderDto> updateDeliveryOrder(@PathVariable String orderId, @RequestBody DeliveryOrderDto dto) {
        DeliveryOrderDto updated = deliveryOrderService.updateDeliveryOrder(orderId, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteDeliveryOrder(@PathVariable String orderId) {
        deliveryOrderService.deleteDeliveryOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<DeliveryOrderDto> updateStatus(@PathVariable String orderId, @RequestBody String status) {
        DeliveryOrderDto updated = deliveryOrderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updated);
    }
}
