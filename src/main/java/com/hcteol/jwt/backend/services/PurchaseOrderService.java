package com.hcteol.jwt.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.PurchaseOrderDto;
import com.hcteol.jwt.backend.dtos.PurchaseOrderItemDto;
import com.hcteol.jwt.backend.entities.PurchaseOrder;
import com.hcteol.jwt.backend.entities.PurchaseOrderItem;
import com.hcteol.jwt.backend.repositories.PurchaseOrderItemRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderRepository;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.StockViewRepository stockViewRepository;

    /**
     * Get product purchase statistics (average cost, latest, high, low, total
     * qty, vendor summaries)
     */
    public com.hcteol.jwt.backend.dtos.ProductPurchaseStatsDto getProductPurchaseStats(Long productId) {
        var items = purchaseOrderItemRepository.findByInternalOrderId(productId);

        // If no items found by internalOrderId, fallback to using StockView references
        if (items == null || items.isEmpty()) {
            java.util.List<com.hcteol.jwt.backend.entities.StockView> svs = stockViewRepository.findByProductId(productId);
            if (svs == null || svs.isEmpty()) {
                return com.hcteol.jwt.backend.dtos.ProductPurchaseStatsDto.builder()
                        .productId(productId)
                        .averageCost(0.0)
                        .latestCost(0.0)
                        .highestCost(0.0)
                        .lowestCost(0.0)
                        .totalQuantity(0L)
                        .vendorSummaries(java.util.Collections.emptyList())
                        .build();
            }

            // use productCode from stock view for matching PO items
            String productCode = null;
            for (var sv : svs) {
                if (sv.getProductCode() != null && !sv.getProductCode().isBlank()) {
                    productCode = sv.getProductCode();
                    break;
                }
            }

            java.util.Set<String> candidateOrderIds = new java.util.HashSet<>();
            for (var sv : svs) {
                String ref = sv.getReference();
                if (ref != null && !ref.isBlank()) {
                    // accept direct matches only
                    if (purchaseOrderRepository.existsById(ref)) {
                        candidateOrderIds.add(ref);
                    }
                }
            }

            java.util.List<com.hcteol.jwt.backend.entities.PurchaseOrderItem> collected = new java.util.ArrayList<>();
            for (String oid : candidateOrderIds) {
                java.util.List<com.hcteol.jwt.backend.entities.PurchaseOrderItem> poItems = purchaseOrderItemRepository.findByOrderId(oid);
                for (var it : poItems) {
                    if (productCode == null || productCode.isBlank()) {
                        // if no productCode available, include all
                        collected.add(it);
                    } else {
                        if (productCode.equalsIgnoreCase(it.getProductCode())) {
                            collected.add(it);
                        }
                    }
                }
            }

            items = collected;
        }

        double sumWeightedCost = 0.0;
        long totalQty = 0L;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;

        // collect items grouped by orderId for latest-cost and per-vendor grouping
        java.util.Map<String, java.util.List<com.hcteol.jwt.backend.entities.PurchaseOrderItem>> itemsByOrder = new java.util.HashMap<>();
        java.util.Map<Long, java.util.List<com.hcteol.jwt.backend.entities.PurchaseOrderItem>> byVendor = new java.util.HashMap<>();

        for (var it : items) {
            Integer qty = it.getQuantity() != null ? it.getQuantity() : 0;
            Double unit = it.getUnitPrice() != null ? it.getUnitPrice() : 0.0;
            sumWeightedCost += unit * qty;
            totalQty += qty;
            if (unit > highest) {
                highest = unit;
            }
            if (unit < lowest) {
                lowest = unit;
            }

            String orderId = it.getOrderId();
            itemsByOrder.computeIfAbsent(orderId != null ? orderId : "", k -> new java.util.ArrayList<>()).add(it);

            var poOpt = purchaseOrderRepository.findById(orderId);
            if (poOpt.isPresent()) {
                Long vendorId = poOpt.get().getVendorId();
                byVendor.computeIfAbsent(vendorId != null ? vendorId : 0L, k -> new java.util.ArrayList<>()).add(it);
            } else {
                byVendor.computeIfAbsent(0L, k -> new java.util.ArrayList<>()).add(it);
            }
        }

        // determine latestCost by finding the orderId with the latest orderDate
        Double latestCost = null;
        java.time.Instant latestInstant = java.time.Instant.EPOCH;
        for (var entry : itemsByOrder.entrySet()) {
            String oid = entry.getKey();
            if (oid == null || oid.isBlank()) {
                continue;
            }
            var poOpt = purchaseOrderRepository.findById(oid);
            if (poOpt.isPresent()) {
                var po = poOpt.get();
                java.sql.Date poDate = po.getOrderDate();
                if (poDate != null) {
                    java.time.Instant inst = poDate.toLocalDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
                    if (inst.isAfter(latestInstant)) {
                        latestInstant = inst;
                        // pick unit price from first matching item in that order
                        var list = entry.getValue();
                        if (list != null && !list.isEmpty()) {
                            Double unit = list.get(0).getUnitPrice() != null ? list.get(0).getUnitPrice() : 0.0;
                            latestCost = unit;
                        }
                    }
                }
            }
        }

        double avg = totalQty > 0 ? (sumWeightedCost / totalQty) : 0.0;

        java.util.List<com.hcteol.jwt.backend.dtos.VendorPurchaseSummaryDto> vendorSummaries = new java.util.ArrayList<>();
        for (var e : byVendor.entrySet()) {
            Long vendorId = e.getKey();
            var list = e.getValue();
            long vQty = 0L;
            double vSum = 0.0;
            double vHigh = Double.MIN_VALUE;
            double vLow = Double.MAX_VALUE;
            for (var it : list) {
                int q = it.getQuantity() != null ? it.getQuantity() : 0;
                double u = it.getUnitPrice() != null ? it.getUnitPrice() : 0.0;
                vQty += q;
                vSum += u * q;
                if (u > vHigh) {
                    vHigh = u;
                }
                if (u < vLow) {
                    vLow = u;
                }
            }
            double vAvg = vQty > 0 ? (vSum / vQty) : 0.0;
            vendorSummaries.add(com.hcteol.jwt.backend.dtos.VendorPurchaseSummaryDto.builder()
                    .vendorId(vendorId)
                    .averageCost(vAvg)
                    .highestCost(vHigh == Double.MIN_VALUE ? 0.0 : vHigh)
                    .lowestCost(vLow == Double.MAX_VALUE ? 0.0 : vLow)
                    .totalQuantity(vQty)
                    .build());
        }

        return com.hcteol.jwt.backend.dtos.ProductPurchaseStatsDto.builder()
                .productId(productId)
                .averageCost(avg)
                .latestCost(latestCost != null ? latestCost : 0.0)
                .highestCost(highest == Double.MIN_VALUE ? 0.0 : highest)
                .lowestCost(lowest == Double.MAX_VALUE ? 0.0 : lowest)
                .totalQuantity(totalQty)
                .vendorSummaries(vendorSummaries)
                .build();
    }

    @Autowired
    private com.hcteol.jwt.backend.services.DocumentSeqService documentSeqService;

    @Autowired
    private AutoHoldMovementService autoHoldMovementService;

    /**
     * Create a new purchase order with its items
     */
    @Transactional
    public PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        // If orderId is null or blank, obtain next PO seq via DocumentSeqService
        if (purchaseOrderDto.getOrderId() == null || purchaseOrderDto.getOrderId().trim().length() == 0) {
            String token = UUID.randomUUID().toString();
            Long seq = documentSeqService.getNextSeq("PO", token);
            purchaseOrderDto.setOrderId("PO-" + seq);
        }
        // Save the purchase order
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .orderId(purchaseOrderDto.getOrderId())
                .vendorId(purchaseOrderDto.getVendorId())
                .projectCode(purchaseOrderDto.getProjectCode())
                .orderDate(purchaseOrderDto.getOrderDate())
                .issuedDate(purchaseOrderDto.getIssuedDate())
                .confirmedDate(purchaseOrderDto.getConfirmedDate())
                .readyDate(purchaseOrderDto.getReadyDate())
                .receivedDate(purchaseOrderDto.getReceivedDate())
                .cancelledDate(purchaseOrderDto.getCancelledDate())
                .purchaseAmount(purchaseOrderDto.getPurchaseAmount())
                .orderStatus(purchaseOrderDto.getOrderStatus() != null ? purchaseOrderDto.getOrderStatus() : "NEW")
                .build();

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        // Save the order items with generated itemId
        List<PurchaseOrderItem> items = new ArrayList<>();
        if (purchaseOrderDto.getItems() != null && !purchaseOrderDto.getItems().isEmpty()) {
            for (int i = 0; i < purchaseOrderDto.getItems().size(); i++) {
                PurchaseOrderItemDto itemDto = purchaseOrderDto.getItems().get(i);
                String itemId = savedOrder.getOrderId() + "_" + (i + 1);

                PurchaseOrderItem item = PurchaseOrderItem.builder()
                        .itemId(itemId)
                        .orderId(savedOrder.getOrderId())
                        .itemType(itemDto.getItemType())
                        .productCode(itemDto.getProductCode())
                        .internalProductCode(itemDto.getInternalProductCode())
                        .internalOrderId(itemDto.getInternalOrderId())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .lineTotal(itemDto.getLineTotal())
                        .build();

                items.add(purchaseOrderItemRepository.save(item));
            }
        }

        return convertToDto(savedOrder, items);
    }

    /**
     * Get all purchase orders
     */
    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
        return orders.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getOrderId());
                    return convertToDto(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a purchase order by ID
     */
    public PurchaseOrderDto getPurchaseOrderById(String orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + orderId));

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        return convertToDto(order, items);
    }

    /**
     * Get purchase orders by vendor ID
     */
    public List<PurchaseOrderDto> getPurchaseOrdersByVendorId(Long vendorId) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findByVendorId(vendorId);
        return orders.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getOrderId());
                    return convertToDto(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get purchase orders by status
     */
    public List<PurchaseOrderDto> getPurchaseOrdersByStatus(String orderStatus) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findByOrderStatus(orderStatus);
        return orders.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getOrderId());
                    return convertToDto(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Update a purchase order
     */
    @Transactional
    public PurchaseOrderDto updatePurchaseOrder(String orderId, PurchaseOrderDto purchaseOrderDto) {
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + orderId));

        // Update order fields
        existingOrder.setVendorId(purchaseOrderDto.getVendorId());
        existingOrder.setProjectCode(purchaseOrderDto.getProjectCode());
        existingOrder.setOrderDate(purchaseOrderDto.getOrderDate());
        existingOrder.setIssuedDate(purchaseOrderDto.getIssuedDate());
        existingOrder.setConfirmedDate(purchaseOrderDto.getConfirmedDate());
        existingOrder.setReadyDate(purchaseOrderDto.getReadyDate());
        existingOrder.setReceivedDate(purchaseOrderDto.getReceivedDate());
        existingOrder.setCancelledDate(purchaseOrderDto.getCancelledDate());
        existingOrder.setPurchaseAmount(purchaseOrderDto.getPurchaseAmount());
        existingOrder.setOrderStatus(purchaseOrderDto.getOrderStatus());

        PurchaseOrder updatedOrder = purchaseOrderRepository.save(existingOrder);

        // Delete existing items and create new ones
        purchaseOrderItemRepository.deleteByOrderId(orderId);

        List<PurchaseOrderItem> items = new ArrayList<>();
        if (purchaseOrderDto.getItems() != null && !purchaseOrderDto.getItems().isEmpty()) {
            for (int i = 0; i < purchaseOrderDto.getItems().size(); i++) {
                PurchaseOrderItemDto itemDto = purchaseOrderDto.getItems().get(i);
                String itemId = orderId + "_" + (i + 1);

                PurchaseOrderItem item = PurchaseOrderItem.builder()
                        .itemId(itemId)
                        .orderId(orderId)
                        .itemType(itemDto.getItemType())
                        .productCode(itemDto.getProductCode())
                        .internalProductCode(itemDto.getInternalProductCode())
                        .internalOrderId(itemDto.getInternalOrderId())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .lineTotal(itemDto.getLineTotal())
                        .build();

                items.add(purchaseOrderItemRepository.save(item));
            }
        }

        return convertToDto(updatedOrder, items);
    }

    /**
     * Delete a purchase order and its items
     */
    @Transactional
    public void deletePurchaseOrder(String orderId) {
        if (!purchaseOrderRepository.existsById(orderId)) {
            throw new RuntimeException("Purchase Order not found with id: " + orderId);
        }

        // Delete all items first
        purchaseOrderItemRepository.deleteByOrderId(orderId);
        autoHoldMovementService.deletePurchaseOrderHolds(orderId);

        // Delete the order
        purchaseOrderRepository.deleteById(orderId);
    }

    /**
     * Update order status
     */
    @Transactional
    public PurchaseOrderDto updateOrderStatus(String orderId, String newStatus) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + orderId));

        order.setOrderStatus(newStatus);
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(order);

        if (newStatus != null && "RECEIVED".equalsIgnoreCase(newStatus.trim())) {
            autoHoldMovementService.deletePurchaseOrderHolds(orderId);
        }

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        return convertToDto(updatedOrder, items);
    }

    /**
     * Helper method to convert entities to DTO
     */
    private PurchaseOrderDto convertToDto(PurchaseOrder order, List<PurchaseOrderItem> items) {
        List<PurchaseOrderItemDto> itemDtos = items.stream()
                .map(item -> PurchaseOrderItemDto.builder()
                .itemId(item.getItemId())
                .orderId(item.getOrderId())
                .itemType(item.getItemType())
                .productCode(item.getProductCode())
                .internalProductCode(item.getInternalProductCode())
                .internalOrderId(item.getInternalOrderId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build())
                .collect(Collectors.toList());

        return PurchaseOrderDto.builder()
                .orderId(order.getOrderId())
                .vendorId(order.getVendorId())
                .projectCode(order.getProjectCode())
                .orderDate(order.getOrderDate())
                .issuedDate(order.getIssuedDate())
                .confirmedDate(order.getConfirmedDate())
                .readyDate(order.getReadyDate())
                .receivedDate(order.getReceivedDate())
                .cancelledDate(order.getCancelledDate())
                .purchaseAmount(order.getPurchaseAmount())
                .orderStatus(order.getOrderStatus())
                .items(itemDtos)
                .build();
    }
}
