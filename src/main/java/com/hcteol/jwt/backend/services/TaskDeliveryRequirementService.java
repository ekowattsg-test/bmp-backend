package com.hcteol.jwt.backend.services;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.DeliveryOrderDto;
import com.hcteol.jwt.backend.dtos.DeliveryOrderItemDto;
import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.Product;
import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.entities.ProjectInventoryView;
import com.hcteol.jwt.backend.entities.PurchaseOrder;
import com.hcteol.jwt.backend.entities.PurchaseOrderItem;
import com.hcteol.jwt.backend.entities.TaskDeliveryRequirement;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProductRepository;
import com.hcteol.jwt.backend.repositories.ProjectInventoryViewRepository;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderItemRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderRepository;
import com.hcteol.jwt.backend.repositories.StockViewRepository;
import com.hcteol.jwt.backend.repositories.TaskDeliveryRequirementRepository;

@Service
public class TaskDeliveryRequirementService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskDeliveryRequirementService.class);

    @Autowired
    private TaskDeliveryRequirementRepository taskDeliveryRequirementRepository;

    @Autowired
    private ProjectInventoryViewRepository projectInventoryViewRepository;

    @Autowired
    private StockViewRepository stockViewRepository;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private DocumentSeqService documentSeqService;

    public TaskDeliveryRequirement addTaskDeliveryRequirement(TaskDeliveryRequirement requirement) {
        return taskDeliveryRequirementRepository.save(
                Objects.requireNonNull(requirement, "requirement cannot be null"));
    }

    public List<TaskDeliveryRequirement> getAllTaskDeliveryRequirements() {
        return taskDeliveryRequirementRepository.findAll();
    }

    public Optional<TaskDeliveryRequirement> getTaskDeliveryRequirementById(Long id) {
        Optional<TaskDeliveryRequirement> result = taskDeliveryRequirementRepository.findById(
                Objects.requireNonNull(id, "id cannot be null"));
        result.ifPresent(this::refreshAvailableQuantity);
        return result;
    }

    public List<TaskDeliveryRequirement> getTaskDeliveryRequirementsByWeekStartDate(String weekStartDate) {
        List<TaskDeliveryRequirement> rows = taskDeliveryRequirementRepository.findByWeekStartDate(
                normalizeString(weekStartDate));
        refreshAvailableQuantities(rows);
        return rows;
    }

    public List<TaskDeliveryRequirement> getTaskDeliveryRequirements(
            String weekStartDate,
            String projectCode,
            Long productId,
            String status,
            String deliveryOrderId) {
        List<TaskDeliveryRequirement> rows = taskDeliveryRequirementRepository.findAll();
        String normWeek = normalizeString(weekStartDate);
        String normProject = normalizeString(projectCode);
        String normStatus = normalizeString(status);
        String normDoId = normalizeString(deliveryOrderId);

        List<TaskDeliveryRequirement> filtered = rows.stream()
                .filter(row -> normWeek == null || Objects.equals(row.getWeekStartDate(), normWeek))
                .filter(row -> normProject == null || Objects.equals(row.getProjectCode(), normProject))
                .filter(row -> productId == null || Objects.equals(row.getProductId(), productId))
                .filter(row -> normStatus == null || equalsIgnoreCase(row.getStatus(), normStatus))
                .filter(row -> normDoId == null || Objects.equals(row.getDeliveryOrderId(), normDoId))
                .collect(Collectors.toList());
        refreshAvailableQuantities(filtered);
        return filtered;
    }

    public TaskDeliveryRequirement updateTaskDeliveryRequirement(Long id, TaskDeliveryRequirement details) {
        TaskDeliveryRequirement existing = taskDeliveryRequirementRepository.findById(
                Objects.requireNonNull(id, "id cannot be null")).orElse(null);
        if (existing == null) {
            return null;
        }

        if (details.getSelected() != null) {
            existing.setSelected(details.getSelected());
            if (details.getSelected() == 1 && existing.getStatus() != null
                    && existing.getStatus().equalsIgnoreCase("EXTRACTED")) {
                existing.setStatus("SELECTED");
            }
        }
        if (details.getDeliveryQuantity() != null) {
            existing.setDeliveryQuantity(details.getDeliveryQuantity());
        }
        if (details.getDeliveryDate() != null && !details.getDeliveryDate().isBlank()) {
            existing.setDeliveryDate(details.getDeliveryDate());
        }
        if (details.getStatus() != null && !details.getStatus().isBlank()) {
            existing.setStatus(details.getStatus());
        }

        return taskDeliveryRequirementRepository.save(existing);
    }

    public void deleteTaskDeliveryRequirement(Long id) {
        taskDeliveryRequirementRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public Map<String, Object> extractTaskInventoryForWeek(String weekStartDate) {
        LocalDate runDate = LocalDate.now();
        LocalDate weekStart = parseWeekStartDate(weekStartDate, runDate);
        LocalDate weekEnd = weekStart.plusDays(6);

        Set<String> activeProjectCodes = projectRepository.findAll().stream()
                .filter(project -> project != null && !isCompletedProject(project.getStatus()))
                .map(Project::getProjectCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<ProjectInventoryView> rows = projectInventoryViewRepository.findAll();
        String centralLocation = getMainWarehouseLocation();

        Map<Long, Long> availableByProduct = new HashMap<>();

        int createdCount = 0;
        int updatedCount = 0;

        for (ProjectInventoryView row : rows) {
            if (!isEligibleInventoryRow(row, weekStart, weekEnd, activeProjectCodes)) {
                continue;
            }

            long requiredQty = Math.round(row.getQuantity());
            if (requiredQty <= 0L) {
                continue;
            }

            Long projectTaskId = resolveProjectTaskId(row);
            Long activityId = row.getActivityId();
            if (activityId == null) {
                continue;
            }

            long availableQty = availableByProduct.computeIfAbsent(
                    row.getProductId(),
                    pid -> computeAvailableQuantityAtLocation(pid, centralLocation));

            long deliveryQty = Math.min(requiredQty, availableQty);

            Optional<TaskDeliveryRequirement> existingOpt = taskDeliveryRequirementRepository
                    .findByWeekStartDateAndActivityIdAndInventoryTypeAndProductId(
                            weekStart.toString(),
                            activityId,
                            row.getInventoryType(),
                            row.getProductId());

            TaskDeliveryRequirement target;
            if (existingOpt.isPresent()) {
                target = existingOpt.get();
                target.setRequiredQuantity(requiredQty);
                target.setAvailableQuantity(availableQty);
                target.setDeliveryQuantity(deliveryQty);
                target.setExtractionDate(runDate.toString());
                if (target.getStatus() == null || target.getStatus().isBlank()) {
                    target.setStatus("EXTRACTED");
                }
                if (target.getSelected() == null) {
                    target.setSelected(0);
                }
                updatedCount++;
            } else {
                target = new TaskDeliveryRequirement();
                target.setProjectCode(row.getProjectCode());
                target.setProjectTaskId(projectTaskId);
                target.setActivityId(activityId);
                target.setActivityId(row.getActivityId());
                target.setActivityName(row.getActivityName());
                target.setInventoryType(row.getInventoryType());
                target.setInventoryId(row.getInventoryId());
                target.setProductId(row.getProductId());
                target.setProductCode(resolveProductCode(row.getProductId()));
                target.setProductName(row.getProductName());
                target.setProductUom(row.getProductUom());
                target.setRequiredQuantity(requiredQty);
                target.setAvailableQuantity(availableQty);
                target.setDeliveryQuantity(deliveryQty);
                target.setSelected(0);
                target.setStatus("EXTRACTED");
                target.setWeekStartDate(weekStart.toString());
                target.setExtractionDate(runDate.toString());
                target.setDeliveryDate(weekStart.toString());
                createdCount++;
            }

            taskDeliveryRequirementRepository.save(target);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("weekStartDate", weekStart.toString());
        response.put("weekEndDate", weekEnd.toString());
        response.put("runDate", runDate.toString());
        response.put("createdCount", createdCount);
        response.put("updatedCount", updatedCount);
        return response;
    }

    @Transactional
    public Map<String, Object> generateDeliveryOrdersFromSelectedRequirements(
            List<TaskDeliveryRequirement> submittedRequirements) {
        if (submittedRequirements == null || submittedRequirements.isEmpty()) {
            throw new IllegalArgumentException("Submitted requirement records are required.");
        }

        List<Long> selectedIds = submittedRequirements.stream()
                .filter(Objects::nonNull)
                .filter(record -> Objects.equals(record.getSelected(), 1))
                .map(TaskDeliveryRequirement::getTaskDeliveryRequirementId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (selectedIds.isEmpty()) {
            throw new IllegalArgumentException("No selected requirement records (selected=1) found in request.");
        }

        Map<Long, TaskDeliveryRequirement> submittedById = new HashMap<>();
        for (TaskDeliveryRequirement record : submittedRequirements) {
            if (record != null && record.getTaskDeliveryRequirementId() != null) {
                submittedById.put(record.getTaskDeliveryRequirementId(), record);
            }
        }

        List<TaskDeliveryRequirement> selectedRecords = taskDeliveryRequirementRepository.findAllById(selectedIds)
                .stream()
                .filter(record -> record.getDeliveryOrderId() == null || record.getDeliveryOrderId().isBlank())
                .peek(this::refreshAvailableQuantity)
                .peek(record -> {
                    TaskDeliveryRequirement submitted = submittedById.get(record.getTaskDeliveryRequirementId());
                    if (submitted != null) {
                        if (submitted.getDeliveryQuantity() != null) {
                            record.setDeliveryQuantity(submitted.getDeliveryQuantity());
                        }
                        if (submitted.getDeliveryDate() != null && !submitted.getDeliveryDate().isBlank()) {
                            record.setDeliveryDate(submitted.getDeliveryDate());
                        }
                    }
                })
                .toList();

        if (selectedRecords.isEmpty()) {
            throw new IllegalArgumentException("No selected requirement records are eligible for DO creation.");
        }

        // Validate and normalize delivery quantities
        for (TaskDeliveryRequirement record : selectedRecords) {
            Long deliveryQty = record.getDeliveryQuantity();
            Long requiredQty = record.getRequiredQuantity();
            Long availableQty = record.getAvailableQuantity();
            if (deliveryQty == null || deliveryQty <= 0L) {
                throw new IllegalArgumentException(
                        "Delivery quantity must be greater than zero for requirement id "
                        + record.getTaskDeliveryRequirementId());
            }
            if (requiredQty != null && deliveryQty > requiredQty) {
                throw new IllegalArgumentException(
                        "Delivery quantity cannot exceed required quantity for requirement id "
                        + record.getTaskDeliveryRequirementId());
            }
            if (availableQty != null && deliveryQty > availableQty) {
                throw new IllegalArgumentException(
                        "Delivery quantity cannot exceed available quantity for requirement id "
                        + record.getTaskDeliveryRequirementId());
            }
        }

        // Group by projectCode + deliveryDate
        Map<String, List<TaskDeliveryRequirement>> recordsByGroup = selectedRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> String.valueOf(record.getProjectCode()) + "|" + String.valueOf(record.getDeliveryDate()),
                        LinkedHashMap::new,
                        Collectors.toList()));

        int createdDoCount = 0;
        int createdItemCount = 0;
        int updatedRequirementCount = 0;
        LocalDate orderDate = LocalDate.now();
        List<String> generatedOrderIds = new ArrayList<>();

        double marginPercent = getDeliveryPriceMargin();

        for (Map.Entry<String, List<TaskDeliveryRequirement>> groupEntry : recordsByGroup.entrySet()) {
            List<TaskDeliveryRequirement> groupRecords = groupEntry.getValue();
            if (groupRecords.isEmpty()) {
                continue;
            }

            String projectCode = groupRecords.get(0).getProjectCode();
            String deliveryDate = groupRecords.get(0).getDeliveryDate();
            Long customerId = resolveCustomerId(projectCode);

            Long doSeq = documentSeqService.getNextSeq("DO", UUID.randomUUID().toString());
            String orderId = "DO-" + doSeq;

            DeliveryOrderDto deliveryOrderDto = new DeliveryOrderDto();
            deliveryOrderDto.setOrderId(orderId);
            deliveryOrderDto.setCustomerId(customerId);
            deliveryOrderDto.setProjectCode(projectCode);
            deliveryOrderDto.setOrderDate(Date.valueOf(orderDate));
            deliveryOrderDto.setDeliveryDate(parseSqlDate(deliveryDate));
            deliveryOrderDto.setOrderStatus("NEW");

            List<DeliveryOrderItemDto> items = new ArrayList<>();
            int itemIndex = 1;
            double orderTotal = 0d;

            Map<Long, ProductAggregate> aggregateByProduct = new LinkedHashMap<>();
            for (TaskDeliveryRequirement record : groupRecords) {
                Long productId = record.getProductId();
                Long deliveryQty = record.getDeliveryQuantity();
                if (productId == null || deliveryQty == null || deliveryQty <= 0L) {
                    continue;
                }

                ProductAggregate aggregate = aggregateByProduct.computeIfAbsent(productId,
                        ignored -> new ProductAggregate(productId));
                aggregate.quantity += deliveryQty;
                aggregate.requirementIds.add(record.getTaskDeliveryRequirementId());
            }

            for (ProductAggregate aggregate : aggregateByProduct.values()) {
                Optional<Product> productOpt = productRepository.findById(aggregate.productId);
                String productCode = productOpt.map(Product::getProductCode).orElse(null);
                String itemType = resolveItemType(productOpt);

                int qtyInt = aggregate.quantity > Integer.MAX_VALUE
                        ? Integer.MAX_VALUE
                        : (int) aggregate.quantity;
                Double averageCost = computeAverageUnitCost(productCode);
                Double unitPrice = applyMargin(averageCost, marginPercent);
                Double lineTotal = unitPrice == null ? null : unitPrice * qtyInt;
                if (lineTotal != null) {
                    orderTotal += lineTotal;
                }

                DeliveryOrderItemDto item = DeliveryOrderItemDto.builder()
                        .itemId(orderId + "_" + itemIndex++)
                        .orderId(orderId)
                        .itemType(itemType)
                        .productCode(productCode)
                        .internalProductCode(String.valueOf(aggregate.productId))
                        .internalOrderId(aggregate.requirementIds.get(0))
                        .quantity(qtyInt)
                        .unitPrice(unitPrice)
                        .lineTotal(lineTotal)
                        .build();
                items.add(item);
                createdItemCount++;
            }

            deliveryOrderDto.setDeliveryAmount(orderTotal);
            deliveryOrderDto.setItems(items);
            DeliveryOrderDto savedOrder = deliveryOrderService.createDeliveryOrder(deliveryOrderDto);
            String savedOrderId = savedOrder.getOrderId() != null && !savedOrder.getOrderId().isBlank()
                    ? savedOrder.getOrderId()
                    : orderId;

            for (TaskDeliveryRequirement record : groupRecords) {
                record.setDeliveryOrderId(savedOrderId);
                record.setStatus("GENERATED");
                record.setSelected(1);
                taskDeliveryRequirementRepository.save(record);
                updatedRequirementCount++;
            }

            generatedOrderIds.add(savedOrderId);
            createdDoCount++;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orderDate", orderDate.toString());
        response.put("selectedCount", selectedIds.size());
        response.put("createdDoCount", createdDoCount);
        response.put("createdItemCount", createdItemCount);
        response.put("updatedRequirementCount", updatedRequirementCount);
        response.put("generatedOrderIds", generatedOrderIds);
        return response;
    }

    private boolean isEligibleInventoryRow(ProjectInventoryView row,
            LocalDate weekStart,
            LocalDate weekEnd,
            Set<String> activeProjectCodes) {
        if (row == null || row.getProductId() == null || row.getProjectCode() == null || row.getQuantity() == null) {
            return false;
        }
        if (!activeProjectCodes.contains(row.getProjectCode())) {
            return false;
        }

        LocalDate effectiveStart = resolveEffectiveStartDate(row);
        if (effectiveStart == null) {
            return false;
        }
        return !effectiveStart.isBefore(weekStart) && !effectiveStart.isAfter(weekEnd);
    }

    private LocalDate resolveEffectiveStartDate(ProjectInventoryView row) {
        String status = normalizeStatus(row.getStatus());
        if ("not started".equals(status)) {
            return parseIsoDate(row.getStartDate());
        }
        LocalDate actualStart = parseIsoDate(row.getActualStartDate());
        if (actualStart != null) {
            return actualStart;
        }
        return parseIsoDate(row.getStartDate());
    }

    private Long resolveProjectTaskId(ProjectInventoryView row) {
        String inventoryType = normalizeString(row.getInventoryType());
        if (inventoryType == null) {
            return null;
        }
        // For stream-level inventory types, activityId is projectStreamId and there is no projectTaskId.
        // For task-level types, activityId is projectTaskId.
        if (inventoryType.equalsIgnoreCase("StreamAsset") || inventoryType.equalsIgnoreCase("StreamBundle")) {
            return null;
        }
        return row.getActivityId();
    }

    private Long computeAvailableQuantityAtLocation(Long productId, String location) {
        if (productId == null) {
            return 0L;
        }
        try {
            return stockViewRepository.getAvailableQuantityByProductIdAndLocation(productId, location);
        } catch (Exception ex) {
            LOG.warn("Failed to compute available quantity for productId={} at location={}", productId, location, ex);
            return 0L;
        }
    }

    private void refreshAvailableQuantity(TaskDeliveryRequirement record) {
        if (record == null || record.getProductId() == null) {
            return;
        }
        String location = getMainWarehouseLocation();
        Long currentAvailable = computeAvailableQuantityAtLocation(record.getProductId(), location);
        record.setAvailableQuantity(currentAvailable);
    }

    private void refreshAvailableQuantities(List<TaskDeliveryRequirement> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        String location = getMainWarehouseLocation();
        for (TaskDeliveryRequirement record : records) {
            if (record == null || record.getProductId() == null) {
                continue;
            }
            Long currentAvailable = computeAvailableQuantityAtLocation(record.getProductId(), location);
            record.setAvailableQuantity(currentAvailable);
        }
    }

    private String getMainWarehouseLocation() {
        return paramRepository.findById("mainWarehouse")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse("central");
    }

    private String resolveProductCode(Long productId) {
        if (productId == null) {
            return null;
        }
        return productRepository.findById(productId)
                .map(Product::getProductCode)
                .orElse(null);
    }

    private String resolveItemType(Optional<Product> productOpt) {
        return productOpt
                .map(Product::getProductCategory)
                .filter(category -> "A".equalsIgnoreCase(category))
                .map(category -> "A")
                .orElse("I");
    }

    private Long resolveCustomerId(String projectCode) {
        if (projectCode == null || projectCode.isBlank()) {
            return null;
        }
        return projectRepository.findById(projectCode)
                .map(Project::getCustomerId)
                .orElse(null);
    }

    private double getDeliveryPriceMargin() {
        return paramRepository.findById("deliveryPriceMargin")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException ex) {
                        return 0d;
                    }
                })
                .orElse(0d);
    }

    private Double computeAverageUnitCost(String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return null;
        }
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByProductCode(productCode.trim());
        if (items.isEmpty()) {
            return null;
        }

        Set<String> orderIds = items.stream()
                .map(PurchaseOrderItem::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> cancelledOrderIds = purchaseOrderRepository.findAllById(orderIds).stream()
                .filter(order -> order.getOrderStatus() != null
                && "CANCELLED".equalsIgnoreCase(order.getOrderStatus().trim()))
                .map(PurchaseOrder::getOrderId)
                .collect(Collectors.toSet());

        double totalCost = 0d;
        long totalQty = 0L;
        for (PurchaseOrderItem item : items) {
            if (item.getOrderId() != null && cancelledOrderIds.contains(item.getOrderId())) {
                continue;
            }
            Integer qty = item.getQuantity();
            Double price = item.getUnitPrice();
            if (qty == null || qty <= 0 || price == null || price < 0) {
                continue;
            }
            totalCost += price * qty;
            totalQty += qty;
        }

        if (totalQty == 0L) {
            return null;
        }
        return totalCost / totalQty;
    }

    private Double applyMargin(Double averageCost, double marginPercent) {
        if (averageCost == null || averageCost < 0) {
            return null;
        }
        double multiplier = 1d + (marginPercent / 100d);
        return averageCost * multiplier;
    }

    private static class ProductAggregate {

        private final Long productId;
        private long quantity = 0L;
        private final List<Long> requirementIds = new ArrayList<>();

        private ProductAggregate(Long productId) {
            this.productId = productId;
        }
    }

    private LocalDate parseWeekStartDate(String weekStartDate, LocalDate defaultDate) {
        if (weekStartDate == null || weekStartDate.isBlank()) {
            return nextMonday(defaultDate);
        }
        LocalDate parsed = parseIsoDate(weekStartDate);
        if (parsed == null) {
            return nextMonday(defaultDate);
        }
        return parsed;
    }

    private LocalDate nextMonday(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate parseIsoDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private java.sql.Date parseSqlDate(String value) {
        LocalDate localDate = parseIsoDate(value);
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    private String normalizeString(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isCompletedProject(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return Set.of("COMPLETE", "CLOSE", "COMPLETED", "CLOSED").contains(normalized);
    }

    private boolean equalsIgnoreCase(String a, String b) {
        return Objects.equals(
                a == null ? null : a.toLowerCase(Locale.ROOT),
                b == null ? null : b.toLowerCase(Locale.ROOT));
    }
}
