package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.DeliveryOrderItemDto;
import com.hcteol.jwt.backend.entities.DeliveryOrderItem;
import com.hcteol.jwt.backend.repositories.DeliveryOrderItemRepository;

@Service
public class DeliveryOrderItemService {

    @Autowired
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Transactional
    public DeliveryOrderItemDto createDeliveryOrderItem(DeliveryOrderItemDto itemDto) {
        DeliveryOrderItem item = DeliveryOrderItem.builder()
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

        DeliveryOrderItem savedItem = deliveryOrderItemRepository.save(item);
        return convertToDto(savedItem);
    }

    public List<DeliveryOrderItemDto> getAllDeliveryOrderItems() {
        List<DeliveryOrderItem> items = deliveryOrderItemRepository.findAll();
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public DeliveryOrderItemDto getDeliveryOrderItemById(String itemId) {
        DeliveryOrderItem item = deliveryOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Delivery Order Item not found with id: " + itemId));
        return convertToDto(item);
    }

    public List<DeliveryOrderItemDto> getDeliveryOrderItemsByOrderId(String orderId) {
        List<DeliveryOrderItem> items = deliveryOrderItemRepository.findByOrderId(orderId);
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public DeliveryOrderItemDto updateDeliveryOrderItem(String itemId, DeliveryOrderItemDto itemDto) {
        DeliveryOrderItem existingItem = deliveryOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Delivery Order Item not found with id: " + itemId));

        existingItem.setOrderId(itemDto.getOrderId());
        existingItem.setItemType(itemDto.getItemType());
        existingItem.setProductCode(itemDto.getProductCode());
        existingItem.setInternalProductCode(itemDto.getInternalProductCode());
        existingItem.setInternalOrderId(itemDto.getInternalOrderId());
        existingItem.setQuantity(itemDto.getQuantity());
        existingItem.setUnitPrice(itemDto.getUnitPrice());
        existingItem.setLineTotal(itemDto.getLineTotal());

        DeliveryOrderItem updatedItem = deliveryOrderItemRepository.save(existingItem);
        return convertToDto(updatedItem);
    }

    @Transactional
    public void deleteDeliveryOrderItem(String itemId) {
        if (!deliveryOrderItemRepository.existsById(itemId)) {
            throw new RuntimeException("Delivery Order Item not found with id: " + itemId);
        }
        deliveryOrderItemRepository.deleteById(itemId);
    }

    @Transactional
    public void deleteDeliveryOrderItemsByOrderId(String orderId) {
        deliveryOrderItemRepository.deleteByOrderId(orderId);
    }

    private DeliveryOrderItemDto convertToDto(DeliveryOrderItem item) {
        return DeliveryOrderItemDto.builder()
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
