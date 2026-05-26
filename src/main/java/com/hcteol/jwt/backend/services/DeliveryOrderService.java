package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

import com.hcteol.jwt.backend.dtos.DeliveryOrderDto;
import com.hcteol.jwt.backend.entities.DeliveryOrder;
import com.hcteol.jwt.backend.repositories.DeliveryOrderRepository;

@Service
public class DeliveryOrderService {

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private com.hcteol.jwt.backend.services.DocumentSeqService documentSeqService;

    @Autowired
    private DeliveryOrderItemService deliveryOrderItemService;

    @Transactional
    public DeliveryOrderDto createDeliveryOrder(DeliveryOrderDto dto) {
        if (dto.getOrderId() == null || dto.getOrderId().trim().length() == 0) {
            String token = UUID.randomUUID().toString();
            Long seq = documentSeqService.getNextSeq("DO", token);
            dto.setOrderId("DO-" + seq);
        }

        dto.setOrderStatus(dto.getOrderStatus() != null ? dto.getOrderStatus() : "NEW");

        DeliveryOrder entity = DeliveryOrder.builder()
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .projectCode(dto.getProjectCode())
                .orderDate(dto.getOrderDate())
                .issuedDate(dto.getIssuedDate())
                .deliveryDate(dto.getDeliveryDate())
                .readyDate(dto.getReadyDate())
                .deliveredDate(dto.getDeliveredDate())
                .cancelledDate(dto.getCancelledDate())
                .deliveryAmount(dto.getDeliveryAmount())
                .orderStatus(dto.getOrderStatus())
                .build();

        DeliveryOrder saved = deliveryOrderRepository.save(entity);

        if (dto.getItems() != null) {
            dto.getItems().forEach(i -> {
                if (i.getItemId() == null || i.getItemId().trim().length() == 0) {
                    i.setItemId(UUID.randomUUID().toString());
                }
                i.setOrderId(saved.getOrderId());
                deliveryOrderItemService.createDeliveryOrderItem(i);
            });
        }

        return convertToDto(saved);
    }

    public List<DeliveryOrderDto> getAllDeliveryOrders() {
        List<DeliveryOrder> all = deliveryOrderRepository.findAll();
        return all.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public DeliveryOrderDto getDeliveryOrderById(String orderId) {
        DeliveryOrder order = deliveryOrderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        return convertToDto(order);
    }

    @Transactional
    public DeliveryOrderDto updateDeliveryOrder(String orderId, DeliveryOrderDto details) {
        var existing = deliveryOrderRepository.findById(orderId).orElse(null);
        if (existing != null) {
            existing.setCustomerId(details.getCustomerId());
            existing.setProjectCode(details.getProjectCode());
            existing.setOrderDate(details.getOrderDate());
            existing.setIssuedDate(details.getIssuedDate());
            existing.setDeliveryDate(details.getDeliveryDate());
            existing.setReadyDate(details.getReadyDate());
            existing.setDeliveredDate(details.getDeliveredDate());
            existing.setCancelledDate(details.getCancelledDate());
            existing.setDeliveryAmount(details.getDeliveryAmount());
            existing.setOrderStatus(details.getOrderStatus());
            DeliveryOrder saved = deliveryOrderRepository.save(existing);

            // replace items
            deliveryOrderItemService.deleteDeliveryOrderItemsByOrderId(orderId);
            if (details.getItems() != null) {
                details.getItems().forEach(i -> {
                    if (i.getItemId() == null || i.getItemId().trim().length() == 0) {
                        i.setItemId(UUID.randomUUID().toString());
                    }
                    i.setOrderId(orderId);
                    deliveryOrderItemService.createDeliveryOrderItem(i);
                });
            }

            return convertToDto(saved);
        }
        return null;
    }

    @Transactional
    public void deleteDeliveryOrder(String orderId) {
        deliveryOrderItemService.deleteDeliveryOrderItemsByOrderId(orderId);
        deliveryOrderRepository.deleteById(orderId);
    }

    @Transactional
    public DeliveryOrderDto updateOrderStatus(String orderId, String newStatus) {
        DeliveryOrder order = deliveryOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Delivery Order not found with id: " + orderId));
        String oldStatus = order.getOrderStatus();
        String oldNorm = oldStatus == null ? null : oldStatus.trim().toUpperCase();
        String newNorm = newStatus == null ? null : newStatus.trim().toUpperCase();

        // only act when status actually changes
        if (!Objects.equals(oldNorm, newNorm)) {
            order.setOrderStatus(newStatus);
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            if (newNorm != null) {
                switch (newNorm) {
                    case "ISSUED":
                        if (order.getIssuedDate() == null) {
                            order.setIssuedDate(today);
                        }
                        break;
                    case "CONFIRMED":
                        // no automatic lifecycle hook for deliveryDate (confirmedDate renamed)
                        break;
                    case "READY":
                        if (order.getReadyDate() == null) {
                            order.setReadyDate(today);
                        }
                        break;
                    case "RDELIVERED":
                    case "DELIVERED":
                        if (order.getDeliveredDate() == null) {
                            order.setDeliveredDate(today);
                        }
                        break;
                    case "CANCELLED":
                        if (order.getCancelledDate() == null) {
                            order.setCancelledDate(today);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        DeliveryOrder saved = deliveryOrderRepository.save(order);
        return convertToDto(saved);
    }

    private DeliveryOrderDto convertToDto(DeliveryOrder order) {
        DeliveryOrderDto dto = DeliveryOrderDto.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .projectCode(order.getProjectCode())
                .orderDate(order.getOrderDate())
                .issuedDate(order.getIssuedDate())
                .deliveryDate(order.getDeliveryDate())
                .readyDate(order.getReadyDate())
                .deliveredDate(order.getDeliveredDate())
                .cancelledDate(order.getCancelledDate())
                .deliveryAmount(order.getDeliveryAmount())
                .orderStatus(order.getOrderStatus())
                .items(deliveryOrderItemService.getDeliveryOrderItemsByOrderId(order.getOrderId()))
                .build();
        return dto;
    }
}
