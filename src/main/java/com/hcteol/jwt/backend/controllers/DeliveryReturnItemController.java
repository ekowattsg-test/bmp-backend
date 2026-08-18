package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.DeliveryReturnItem;
import com.hcteol.jwt.backend.repositories.DeliveryReturnItemRepository;

@RestController
@RequestMapping("/api/deliveryReturnItems")
public class DeliveryReturnItemController {

    @Autowired
    private DeliveryReturnItemRepository deliveryReturnItemRepository;

    @PostMapping
    public DeliveryReturnItem createDeliveryReturnItem(@RequestBody DeliveryReturnItem item) {
        return deliveryReturnItemRepository.save(item);
    }

    @GetMapping("/return/{returnId}")
    public List<DeliveryReturnItem> getItemsByReturnId(@PathVariable Long returnId) {
        return deliveryReturnItemRepository.findByReturnId(returnId);
    }
}
