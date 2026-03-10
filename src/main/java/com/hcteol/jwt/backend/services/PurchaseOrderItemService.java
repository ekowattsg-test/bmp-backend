package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.PurchaseOrderItemDto;
import com.hcteol.jwt.backend.entities.PurchaseOrderItem;
import com.hcteol.jwt.backend.repositories.PurchaseOrderItemRepository;

@Service
public class PurchaseOrderItemService {

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    /**
     * Create a new purchase order item
     */
    @Transactional
    public PurchaseOrderItemDto createPurchaseOrderItem(PurchaseOrderItemDto itemDto) {
        PurchaseOrderItem item = PurchaseOrderItem.builder()
                .itemId(itemDto.getItemId())
                .orderId(itemDto.getOrderId())
                .itemType(itemDto.getItemType())
                .productCode(itemDto.getProductCode())
                .internalProductCode(itemDto.getInternalProductCode())
                .internalOrderId(itemDto.getInternalOrderId())
                .quantity(itemDto.getQuantity())
                .unitPrice(itemDto.getUnitPrice())
                .lineTotal(itemDto.getLineTotal())
                .build();

        PurchaseOrderItem savedItem = purchaseOrderItemRepository.save(item);
        return convertToDto(savedItem);
    }

    /**
     * Get all purchase order items
     */
    public List<PurchaseOrderItemDto> getAllPurchaseOrderItems() {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAll();
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a purchase order item by ID
     */
    public PurchaseOrderItemDto getPurchaseOrderItemById(String itemId) {
        PurchaseOrderItem item = purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Purchase Order Item not found with id: " + itemId));
        return convertToDto(item);
    }

    /**
     * Get all items for a specific order
     */
    public List<PurchaseOrderItemDto> getPurchaseOrderItemsByOrderId(String orderId) {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Update a purchase order item
     */
    @Transactional
    public PurchaseOrderItemDto updatePurchaseOrderItem(String itemId, PurchaseOrderItemDto itemDto) {
        PurchaseOrderItem existingItem = purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Purchase Order Item not found with id: " + itemId));

        existingItem.setOrderId(itemDto.getOrderId());
        existingItem.setItemType(itemDto.getItemType());
        existingItem.setProductCode(itemDto.getProductCode());
        existingItem.setInternalProductCode(itemDto.getInternalProductCode());
        existingItem.setInternalOrderId(itemDto.getInternalOrderId());
        existingItem.setQuantity(itemDto.getQuantity());
        existingItem.setUnitPrice(itemDto.getUnitPrice());
        existingItem.setLineTotal(itemDto.getLineTotal());

        PurchaseOrderItem updatedItem = purchaseOrderItemRepository.save(existingItem);
        return convertToDto(updatedItem);
    }

    /**
     * Delete a purchase order item
     */
    @Transactional
    public void deletePurchaseOrderItem(String itemId) {
        if (!purchaseOrderItemRepository.existsById(itemId)) {
            throw new RuntimeException("Purchase Order Item not found with id: " + itemId);
        }
        purchaseOrderItemRepository.deleteById(itemId);
    }

    /**
     * Delete all items for a specific order
     */
    @Transactional
    public void deletePurchaseOrderItemsByOrderId(String orderId) {
        purchaseOrderItemRepository.deleteByOrderId(orderId);
    }

    /**
     * Helper method to convert entity to DTO
     */
    private PurchaseOrderItemDto convertToDto(PurchaseOrderItem item) {
        return PurchaseOrderItemDto.builder()
                .itemId(item.getItemId())
                .orderId(item.getOrderId())
                .itemType(item.getItemType())
                .productCode(item.getProductCode())
                .internalProductCode(item.getInternalProductCode())
                .internalOrderId(item.getInternalOrderId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
