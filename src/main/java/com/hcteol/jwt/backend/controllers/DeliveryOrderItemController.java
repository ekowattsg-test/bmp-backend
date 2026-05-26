package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hcteol.jwt.backend.dtos.DeliveryOrderItemDto;
import com.hcteol.jwt.backend.services.DeliveryOrderItemService;

@RestController
@RequestMapping("/api/deliveryOrderItems")
public class DeliveryOrderItemController {

    @Autowired
    private DeliveryOrderItemService deliveryOrderItemService;

    @PostMapping
    public ResponseEntity<DeliveryOrderItemDto> createDeliveryOrderItem(@RequestBody DeliveryOrderItemDto itemDto) {
        try {
            DeliveryOrderItemDto created = deliveryOrderItemService.createDeliveryOrderItem(itemDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DeliveryOrderItemDto>> getAllDeliveryOrderItems() {
        return ResponseEntity.ok(deliveryOrderItemService.getAllDeliveryOrderItems());
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<DeliveryOrderItemDto> getDeliveryOrderItemById(@PathVariable String itemId) {
        try {
            return ResponseEntity.ok(deliveryOrderItemService.getDeliveryOrderItemById(itemId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryOrderItemDto>> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(deliveryOrderItemService.getDeliveryOrderItemsByOrderId(orderId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<DeliveryOrderItemDto> updateDeliveryOrderItem(@PathVariable String itemId, @RequestBody DeliveryOrderItemDto dto) {
        try {
            return ResponseEntity.ok(deliveryOrderItemService.updateDeliveryOrderItem(itemId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteDeliveryOrderItem(@PathVariable String itemId) {
        try {
            deliveryOrderItemService.deleteDeliveryOrderItem(itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> deleteByOrder(@PathVariable String orderId) {
        deliveryOrderItemService.deleteDeliveryOrderItemsByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
