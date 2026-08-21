package com.hcteol.jwt.backend.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.DeliveryOrder;
import com.hcteol.jwt.backend.entities.DeliveryOrderItem;
import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.Product;
import com.hcteol.jwt.backend.entities.PurchaseOrder;
import com.hcteol.jwt.backend.entities.PurchaseOrderItem;
import com.hcteol.jwt.backend.entities.Stock;
import com.hcteol.jwt.backend.entities.StockMovement;
import com.hcteol.jwt.backend.repositories.DeliveryOrderItemRepository;
import com.hcteol.jwt.backend.repositories.DeliveryOrderRepository;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProductRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderItemRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderRepository;
import com.hcteol.jwt.backend.repositories.StockMovementRepository;
import com.hcteol.jwt.backend.repositories.StockRepository;

@Service
public class AutoHoldMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(AutoHoldMovementService.class);

    private static final String DO_HOLD_MOVEMENT_TYPE = "H";
    private static final String PO_HOLD_MOVEMENT_TYPE = "Q";

    private static final Set<String> DO_TERMINAL_STATUSES = Set.of("IN_TRANSIT", "DELIVERED", "CANCELLED");
    private static final Set<String> PO_TERMINAL_STATUSES = Set.of("RECEIVED", "CANCELLED");

    @Value("${auto.generate.hold.movements:false}")
    private boolean autoGenerateHoldMovements;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    public boolean isEnabled() {
        return autoGenerateHoldMovements;
    }

    @Transactional
    public Map<String, Object> rebuildAllHolds() {
        Map<String, Object> result = new HashMap<>();
        if (!autoGenerateHoldMovements) {
            result.put("enabled", false);
            result.put("message", "auto.generate.hold.movements is disabled");
            return result;
        }

        int doRebuilt = 0;
        int doCleared = 0;
        int poRebuilt = 0;
        int poCleared = 0;

        List<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAll();
        for (DeliveryOrder order : deliveryOrders) {
            String orderId = order.getOrderId();
            String status = normalizeStatus(order.getOrderStatus());
            if (orderId == null || orderId.isBlank()) {
                continue;
            }
            if (DO_TERMINAL_STATUSES.contains(status)) {
                deleteDeliveryOrderHolds(orderId);
                doCleared++;
            } else {
                List<DeliveryOrderItem> items = deliveryOrderItemRepository.findByOrderId(orderId);
                if (items != null) {
                    for (DeliveryOrderItem item : items) {
                        createOrUpdateDeliveryOrderItemHold(item);
                    }
                }
                doRebuilt++;
            }
        }

        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        for (PurchaseOrder order : purchaseOrders) {
            String orderId = order.getOrderId();
            String status = normalizeStatus(order.getOrderStatus());
            if (orderId == null || orderId.isBlank()) {
                continue;
            }
            if (PO_TERMINAL_STATUSES.contains(status)) {
                deletePurchaseOrderHolds(orderId);
                poCleared++;
            } else {
                List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
                if (items != null) {
                    for (PurchaseOrderItem item : items) {
                        createOrUpdatePurchaseOrderItemHold(item);
                    }
                }
                poRebuilt++;
            }
        }

        result.put("enabled", true);
        result.put("deliveryOrdersRebuilt", doRebuilt);
        result.put("deliveryOrdersCleared", doCleared);
        result.put("purchaseOrdersRebuilt", poRebuilt);
        result.put("purchaseOrdersCleared", poCleared);
        LOG.info("Rebuilt hold movements: DO rebuilt={}, DO cleared={}, PO rebuilt={}, PO cleared={}",
                doRebuilt, doCleared, poRebuilt, poCleared);
        return result;
    }

    private String normalizeStatus(String status) {
        return status == null ? null : status.trim().toUpperCase();
    }

    @Transactional
    public void createOrUpdateDeliveryOrderItemHold(DeliveryOrderItem item) {
        if (!autoGenerateHoldMovements || item == null) {
            return;
        }
        Long productId = resolveProductId(item.getProductCode(), item.getInternalProductCode());
        if (productId == null) {
            LOG.warn("Cannot create DO hold movement for item {}: no product resolved", item.getItemId());
            return;
        }
        Stock stock = findOrCreateStock(productId, getSystemStockCode());
        deleteExistingHold(stock.getStockId(), item.getOrderId(), DO_HOLD_MOVEMENT_TYPE);
        createHoldMovement(stock.getStockId(), item.getQuantity(), item.getOrderId(), DO_HOLD_MOVEMENT_TYPE);
    }

    @Transactional
    public void deleteDeliveryOrderHolds(String orderId) {
        if (!autoGenerateHoldMovements || orderId == null || orderId.isBlank()) {
            return;
        }
        deleteHoldsByReference(orderId, DO_HOLD_MOVEMENT_TYPE);
    }

    @Transactional
    public void resyncDeliveryOrderHolds(String orderId,
            java.util.function.Supplier<List<DeliveryOrderItem>> itemsSupplier) {
        if (!autoGenerateHoldMovements || orderId == null || orderId.isBlank()) {
            return;
        }
        deleteHoldsByReference(orderId, DO_HOLD_MOVEMENT_TYPE);
        List<DeliveryOrderItem> items = itemsSupplier.get();
        if (items != null) {
            for (DeliveryOrderItem item : items) {
                createOrUpdateDeliveryOrderItemHold(item);
            }
        }
    }

    @Transactional
    public void createOrUpdatePurchaseOrderItemHold(PurchaseOrderItem item) {
        if (!autoGenerateHoldMovements || item == null) {
            return;
        }
        Long productId = resolveProductId(item.getProductCode(), item.getInternalProductCode());
        if (productId == null) {
            LOG.warn("Cannot create PO hold movement for item {}: no product resolved", item.getItemId());
            return;
        }
        Stock stock = findOrCreateStock(productId, getSystemStockCode());
        deleteExistingHold(stock.getStockId(), item.getOrderId(), PO_HOLD_MOVEMENT_TYPE);
        createHoldMovement(stock.getStockId(), item.getQuantity(), item.getOrderId(), PO_HOLD_MOVEMENT_TYPE);
    }

    @Transactional
    public void deletePurchaseOrderHolds(String orderId) {
        if (!autoGenerateHoldMovements || orderId == null || orderId.isBlank()) {
            return;
        }
        deleteHoldsByReference(orderId, PO_HOLD_MOVEMENT_TYPE);
    }

    @Transactional
    public void resyncPurchaseOrderHolds(String orderId,
            java.util.function.Supplier<List<PurchaseOrderItem>> itemsSupplier) {
        if (!autoGenerateHoldMovements || orderId == null || orderId.isBlank()) {
            return;
        }
        deleteHoldsByReference(orderId, PO_HOLD_MOVEMENT_TYPE);
        List<PurchaseOrderItem> items = itemsSupplier.get();
        if (items != null) {
            for (PurchaseOrderItem item : items) {
                createOrUpdatePurchaseOrderItemHold(item);
            }
        }
    }

    private Long resolveProductId(String productCode, String internalProductCode) {
        if (internalProductCode != null && !internalProductCode.isBlank()) {
            try {
                return Long.valueOf(internalProductCode.trim());
            } catch (NumberFormatException ex) {
                // fall through to productCode lookup
            }
        }
        if (productCode != null && !productCode.isBlank()) {
            List<Product> products = productRepository.findAll();
            for (Product product : products) {
                if (productCode.trim().equalsIgnoreCase(product.getProductCode())) {
                    return product.getProductId();
                }
            }
        }
        return null;
    }

    private Stock findOrCreateStock(Long productId, String stockCode) {
        Stock stock = stockRepository.findByProductIdAndStockCode(productId, stockCode);
        if (stock != null) {
            return stock;
        }
        stock = new Stock();
        stock.setProductId(productId);
        stock.setStockCode(stockCode);
        stock.setCreateDate(LocalDate.now().toString());
        return stockRepository.save(stock);
    }

    private void deleteExistingHold(Long stockId, String reference, String movementType) {
        List<StockMovement> existing = stockMovementRepository.findByStockIdAndReference(stockId, reference);
        for (StockMovement movement : existing) {
            if (movementType.equalsIgnoreCase(movement.getMovementType())) {
                stockMovementRepository.delete(movement);
            }
        }
    }

    private void deleteHoldsByReference(String reference, String movementType) {
        List<StockMovement> existing = stockMovementRepository.findByReferenceAndMovementType(reference, movementType);
        for (StockMovement movement : existing) {
            if (movement != null) {
                stockMovementRepository.delete(movement);
            }
        }
    }

    private void createHoldMovement(Long stockId, Integer quantity, String reference, String movementType) {
        if (quantity == null || quantity <= 0) {
            return;
        }
        StockMovement movement = new StockMovement();
        movement.setStockId(stockId);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setLocation(getMainWarehouseLocation());
        movement.setReference(reference);
        movement.setWorkOrderId(null);
        movement.setRecordDate(LocalDate.now().toString());
        movement.setActionBy(null);
        stockMovementRepository.save(movement);
    }

    private String getMainWarehouseLocation() {
        return paramRepository.findById("mainWarehouse")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse("central");
    }

    private String getSystemStockCode() {
        return paramRepository.findById("systemStockCode")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse("SYSHOLD");
    }
}
