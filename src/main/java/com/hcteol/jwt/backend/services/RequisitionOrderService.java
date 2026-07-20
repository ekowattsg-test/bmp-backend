package com.hcteol.jwt.backend.services;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.Product;
import com.hcteol.jwt.backend.entities.ProjectAsset;
import com.hcteol.jwt.backend.entities.ProjectBundle;
import com.hcteol.jwt.backend.entities.ProjectInventoryView;
import com.hcteol.jwt.backend.entities.ProjectStock;
import com.hcteol.jwt.backend.entities.ProjectStreamAsset;
import com.hcteol.jwt.backend.entities.ProjectStreamBundle;
import com.hcteol.jwt.backend.entities.PurchaseOrder;
import com.hcteol.jwt.backend.entities.PurchaseOrderItem;
import com.hcteol.jwt.backend.entities.RequisitionCycle;
import com.hcteol.jwt.backend.entities.RequisitionOrder;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProductRepository;
import com.hcteol.jwt.backend.repositories.ProjectAssetRepository;
import com.hcteol.jwt.backend.repositories.ProjectBundleRepository;
import com.hcteol.jwt.backend.repositories.ProjectInventoryViewRepository;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.ProjectStockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamAssetRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamBundleRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderItemRepository;
import com.hcteol.jwt.backend.repositories.PurchaseOrderRepository;
import com.hcteol.jwt.backend.repositories.RequisitionCycleRepository;
import com.hcteol.jwt.backend.repositories.RequisitionOrderRepository;

@Service
public class RequisitionOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionOrderService.class);

    @Autowired
    private RequisitionOrderRepository requisitionOrderRepository;

    @Autowired
    private RequisitionCycleRepository requisitionCycleRepository;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectInventoryViewRepository projectInventoryViewRepository;

    @Autowired
    private ProjectAssetRepository projectAssetRepository;

    @Autowired
    private ProjectStockRepository projectStockRepository;

    @Autowired
    private ProjectBundleRepository projectBundleRepository;

    @Autowired
    private ProjectStreamAssetRepository projectStreamAssetRepository;

    @Autowired
    private ProjectStreamBundleRepository projectStreamBundleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private DocumentSeqService documentSeqService;

    public RequisitionOrder addRequisitionOrder(RequisitionOrder requisitionOrder) {
        return requisitionOrderRepository.save(
                Objects.requireNonNull(requisitionOrder, "requisitionOrder cannot be null"));
    }

    public List<RequisitionOrder> getAllRequisitionOrders() {
        return requisitionOrderRepository.findAll();
    }

    public List<RequisitionOrder> getRequisitionOrdersReadyForPurchaseOrder() {
        List<RequisitionOrder> rows = requisitionOrderRepository.findByPurchaseOrderIdIsNull();

        System.out.println("DEBUG READY-FOR-PO: repository returned " + rows.size() + " row(s)");
        LOG.info("DEBUG READY-FOR-PO: repository returned {} row(s)", rows.size());
        for (RequisitionOrder row : rows) {
            System.out.println(
                    "DEBUG READY-FOR-PO ROW: requisitionOrderId=" + row.getRequisitionOrderId()
                    + ", purchaseOrderId='" + row.getPurchaseOrderId()
                    + "', status='" + row.getStatus()
                    + "', selected=" + row.getSelected()
                    + ", projectCode='" + row.getProjectCode()
                    + "', productRequested=" + row.getProductRequested());
            LOG.info(
                    "DEBUG READY-FOR-PO ROW: requisitionOrderId={}, purchaseOrderId='{}', status='{}', selected={}, projectCode='{}', productRequested={}",
                    row.getRequisitionOrderId(),
                    row.getPurchaseOrderId(),
                    row.getStatus(),
                    row.getSelected(),
                    row.getProjectCode(),
                    row.getProductRequested());
        }

        List<RequisitionOrder> invalidRows = rows.stream()
                .filter(row -> normalizeString(row.getPurchaseOrderId()) != null)
                .toList();

        if (!invalidRows.isEmpty()) {
            List<Long> sampleIds = invalidRows.stream()
                    .map(RequisitionOrder::getRequisitionOrderId)
                    .filter(Objects::nonNull)
                    .limit(10)
                    .toList();
            LOG.error(
                    "DEBUG CHECK FAILED: findByPurchaseOrderIdIsNull returned {} rows with non-null purchaseOrderId. Sample requisitionOrderIds={}",
                    invalidRows.size(),
                    sampleIds);
        }

        return rows;
    }

    public List<RequisitionOrder> getRequisitionOrders(Long requisitionCycleId,
            String projectCode,
            Long productRequested,
            String purchaseOrderId) {
        return getRequisitionOrdersByCoreFilters(
                requisitionCycleId,
                projectCode,
                productRequested,
                purchaseOrderId);
    }

    private List<RequisitionOrder> getRequisitionOrdersByCoreFilters(Long requisitionCycleId,
            String projectCode,
            Long productRequested,
            String purchaseOrderId) {
        String normalizedProjectCode = normalizeString(projectCode);
        Long normalizedProductRequested = productRequested;
        String normalizedPurchaseOrderId = normalizeString(purchaseOrderId);

        if (requisitionCycleId != null && normalizedProjectCode != null && normalizedProductRequested != null
                && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndProjectCodeAndProductRequestedAndPurchaseOrderId(
                    requisitionCycleId,
                    normalizedProjectCode,
                    normalizedProductRequested,
                    normalizedPurchaseOrderId);
        }

        if (requisitionCycleId != null && normalizedProjectCode != null && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndProjectCodeAndPurchaseOrderId(
                    requisitionCycleId,
                    normalizedProjectCode,
                    normalizedPurchaseOrderId);
        }

        if (requisitionCycleId != null && normalizedProductRequested != null && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndProductRequestedAndPurchaseOrderId(
                    requisitionCycleId,
                    normalizedProductRequested,
                    normalizedPurchaseOrderId);
        }

        if (normalizedProjectCode != null && normalizedProductRequested != null && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByProjectCodeAndProductRequestedAndPurchaseOrderId(
                    normalizedProjectCode,
                    normalizedProductRequested,
                    normalizedPurchaseOrderId);
        }

        if (requisitionCycleId != null && normalizedProjectCode != null && normalizedProductRequested != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndProjectCodeAndProductRequested(
                    requisitionCycleId,
                    normalizedProjectCode,
                    normalizedProductRequested);
        }

        if (requisitionCycleId != null && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndPurchaseOrderId(
                    requisitionCycleId,
                    normalizedPurchaseOrderId);
        }

        if (normalizedProjectCode != null && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByProjectCodeAndPurchaseOrderId(
                    normalizedProjectCode,
                    normalizedPurchaseOrderId);
        }

        if (normalizedProductRequested != null && normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByProductRequestedAndPurchaseOrderId(
                    normalizedProductRequested,
                    normalizedPurchaseOrderId);
        }

        if (requisitionCycleId != null && normalizedProjectCode != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndProjectCode(
                    requisitionCycleId,
                    normalizedProjectCode);
        }
        if (requisitionCycleId != null && normalizedProductRequested != null) {
            return requisitionOrderRepository.findByRequisitionCycleIdAndProductRequested(
                    requisitionCycleId,
                    normalizedProductRequested);
        }
        if (normalizedProjectCode != null && normalizedProductRequested != null) {
            return requisitionOrderRepository.findByProjectCodeAndProductRequested(
                    normalizedProjectCode,
                    normalizedProductRequested);
        }
        if (requisitionCycleId != null) {
            return requisitionOrderRepository.findByRequisitionCycleId(requisitionCycleId);
        }
        if (normalizedProjectCode != null) {
            return requisitionOrderRepository.findByProjectCode(normalizedProjectCode);
        }
        if (normalizedProductRequested != null) {
            return requisitionOrderRepository.findByProductRequested(normalizedProductRequested);
        }
        if (normalizedPurchaseOrderId != null) {
            return requisitionOrderRepository.findByPurchaseOrderId(normalizedPurchaseOrderId);
        }
        return requisitionOrderRepository.findByPurchaseOrderIdIsNull();
    }

    public Optional<RequisitionOrder> getRequisitionOrderById(Long id) {
        return requisitionOrderRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public RequisitionOrder updateRequisitionOrder(Long id, RequisitionOrder details) {
        RequisitionOrder existing = requisitionOrderRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing,
                "requisitionOrderId");
        return requisitionOrderRepository.save(existing);
    }

    public void deleteRequisitionOrder(Long id) {
        requisitionOrderRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public Map<String, Object> createPurchaseOrdersFromSelectedRequisitions(List<RequisitionOrder> submittedRequisitions) {
        if (submittedRequisitions == null || submittedRequisitions.isEmpty()) {
            throw new IllegalArgumentException("Submitted requisition records are required.");
        }

        List<Long> selectedIds = submittedRequisitions.stream()
                .filter(Objects::nonNull)
                .filter(record -> Objects.equals(record.getSelected(), 1))
                .map(RequisitionOrder::getRequisitionOrderId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (selectedIds.isEmpty()) {
            throw new IllegalArgumentException("No selected requisition records (selected=1) found in request.");
        }

        List<RequisitionOrder> selectedRecords = requisitionOrderRepository.findAllById(selectedIds).stream()
                .filter(record -> record.getPurchaseOrderId() == null || record.getPurchaseOrderId().isBlank())
                .toList();

        if (selectedRecords.isEmpty()) {
            throw new IllegalArgumentException("No selected requisition records are eligible for PO creation.");
        }

        Map<Long, List<RequisitionOrder>> recordsByVendor = selectedRecords.stream()
                .filter(record -> record.getVendorPurchased() != null)
                .collect(Collectors.groupingBy(RequisitionOrder::getVendorPurchased));

        int createdPoCount = 0;
        int updatedRequisitionCount = 0;
        int createdPoItemCount = 0;
        int skippedCount = selectedRecords.size() - recordsByVendor.values().stream().mapToInt(List::size).sum();
        LocalDate purchaseDate = LocalDate.now();
        List<String> generatedOrderIds = new ArrayList<>();

        for (Map.Entry<Long, List<RequisitionOrder>> vendorEntry : recordsByVendor.entrySet()) {
            Long vendorId = vendorEntry.getKey();
            List<RequisitionOrder> vendorRecords = vendorEntry.getValue();

            Map<Long, ProductAggregate> aggregateByProduct = new LinkedHashMap<>();
            for (RequisitionOrder record : vendorRecords) {
                Long productId = resolveProductId(record);
                Long quantity = resolveQuantity(record);

                if (productId == null || quantity == null || quantity <= 0L) {
                    skippedCount++;
                    continue;
                }

                ProductAggregate aggregate = aggregateByProduct.computeIfAbsent(productId,
                        ignored -> new ProductAggregate(productId));
                aggregate.quantity += quantity;
                if (aggregate.unitPrice == null && record.getUnitPrice() != null) {
                    aggregate.unitPrice = record.getUnitPrice();
                }
                if (aggregate.unitPrice == null && record.getPriceSuggested() != null) {
                    aggregate.unitPrice = record.getPriceSuggested();
                }
                aggregate.requisitionIds.add(record.getRequisitionOrderId());
            }

            if (aggregateByProduct.isEmpty()) {
                continue;
            }

            Long poSeq = documentSeqService.getNextSeq("PO", UUID.randomUUID().toString());
            String orderId = "PO-" + poSeq;

            PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                    .orderId(orderId)
                    .vendorId(vendorId)
                    .orderDate(Date.valueOf(purchaseDate))
                    .orderStatus("NEW")
                    .purchaseAmount(0d)
                    .build();
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

            int itemIndex = 1;
            double totalAmount = 0d;
            for (ProductAggregate aggregate : aggregateByProduct.values()) {
                Optional<Product> productOpt = productRepository.findById(aggregate.productId);
                String productCode = productOpt.map(Product::getProductCode).orElse(null);

                int qtyInt = aggregate.quantity > Integer.MAX_VALUE ? Integer.MAX_VALUE : aggregate.quantity.intValue();
                Double unitPrice = aggregate.unitPrice;
                Double lineTotal = unitPrice == null ? null : unitPrice * qtyInt;
                if (lineTotal != null) {
                    totalAmount += lineTotal;
                }

                PurchaseOrderItem item = PurchaseOrderItem.builder()
                        .itemId(orderId + "_" + itemIndex++)
                        .orderId(orderId)
                        .itemType("I")
                        .productCode(productCode)
                        .internalProductCode(String.valueOf(aggregate.productId))
                        .quantity(qtyInt)
                        .unitPrice(unitPrice)
                        .lineTotal(lineTotal)
                        .build();
                purchaseOrderItemRepository.save(item);
                createdPoItemCount++;
            }

            purchaseOrder.setPurchaseAmount(totalAmount);
            purchaseOrderRepository.save(purchaseOrder);
            String savedOrderId = purchaseOrder.getOrderId();

            for (RequisitionOrder record : vendorRecords) {
                Long productId = resolveProductId(record);
                Long quantity = resolveQuantity(record);
                if (productId == null || quantity == null || quantity <= 0L) {
                    continue;
                }
                record.setPurchaseOrderId(savedOrderId);
                record.setPurchaseDate(purchaseDate.toString());
                record.setStatus("generated");
                requisitionOrderRepository.save(record);
                updatedRequisitionCount++;
            }

            generatedOrderIds.add(savedOrderId);
            createdPoCount++;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("purchaseDate", purchaseDate.toString());
        response.put("selectedCount", selectedIds.size());
        response.put("createdPoCount", createdPoCount);
        response.put("createdPoItemCount", createdPoItemCount);
        response.put("updatedRequisitionCount", updatedRequisitionCount);
        response.put("skippedCount", skippedCount);
        response.put("generatedOrderIds", generatedOrderIds);
        return response;
    }

    private Long resolveProductId(RequisitionOrder record) {
        if (record.getProductPurchased() != null) {
            return record.getProductPurchased();
        }
        return record.getProductRequested();
    }

    private Long resolveQuantity(RequisitionOrder record) {
        if (record.getQuantityPurchased() != null) {
            return record.getQuantityPurchased();
        }
        return record.getQuantityRequested();
    }

    private static class ProductAggregate {

        private final Long productId;
        private Long quantity = 0L;
        private Double unitPrice;
        private final List<Long> requisitionIds = new ArrayList<>();

        private ProductAggregate(Long productId) {
            this.productId = productId;
        }
    }

    @Transactional
    public Map<String, Object> generateRequisitionOrders(Long requisitionCycleId) {
        return processRequisitionOrders(requisitionCycleId, false);
    }

    @Transactional
    public Map<String, Object> reconcileRequisitionOrders(Long requisitionCycleId) {
        return processRequisitionOrders(requisitionCycleId, true);
    }

    private Map<String, Object> processRequisitionOrders(Long requisitionCycleId, boolean reconcileMode) {
        LocalDate runDate = LocalDate.now();

        ProcurementCycleConfig cycleConfig = getProcurementCycleConfig();
        CyclePeriod cyclePeriod = resolveCyclePeriod(requisitionCycleId, runDate, cycleConfig);
        RequisitionCycle cycle = cyclePeriod.cycle();

        Set<String> activeProjectCodes = projectRepository.findAll().stream()
                .filter(project -> project != null && !isCompletedProject(project.getStatus()))
                .map(project -> project.getProjectCode())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<ProjectInventoryView> rows = projectInventoryViewRepository.findAll();
        Map<RequisitionKey, Long> quantityByKey = new HashMap<>();
        List<ProjectInventoryView> toTagRows = new java.util.ArrayList<>();

        for (ProjectInventoryView row : rows) {
            if (!isEligibleInventoryRow(row, cycle, cyclePeriod, activeProjectCodes, reconcileMode)) {
                continue;
            }

            long qty = Math.round(row.getQuantity());
            if (qty <= 0L) {
                continue;
            }

            RequisitionKey key = new RequisitionKey(row.getProjectCode(), row.getProductId());
            quantityByKey.merge(key, qty, (a, b) -> a + b);

            if (row.getRequisitionCycleId() == null) {
                toTagRows.add(row);
            }
        }

        List<RequisitionOrder> existingForCycle = requisitionOrderRepository.findByRequisitionCycleId(cycle.getRequisitionCycleId());
        Map<RequisitionKey, RequisitionOrder> existingByKey = new HashMap<>();
        for (RequisitionOrder ro : existingForCycle) {
            if (ro.getProjectCode() == null || ro.getProductRequested() == null) {
                continue;
            }
            RequisitionKey key = new RequisitionKey(ro.getProjectCode(), ro.getProductRequested());
            existingByKey.putIfAbsent(key, ro);
        }

        int createdCount = 0;
        int updatedCount = 0;
        int taggedInventoryCount = 0;

        for (Map.Entry<RequisitionKey, Long> entry : quantityByKey.entrySet()) {
            RequisitionKey key = entry.getKey();
            long incomingQty = entry.getValue();
            if (incomingQty <= 0L) {
                continue;
            }

            RequisitionOrder target;
            RequisitionOrder existing = existingByKey.get(key);
            if (existing == null) {
                target = new RequisitionOrder();
                target.setRequisitionCycleId(cycle.getRequisitionCycleId());
                target.setProjectCode(key.projectCode());
                target.setProductRequested(key.productId());
                target.setProductPurchased(key.productId());
                target.setQuantityRequested(incomingQty);
                target.setRequisitionDate(runDate.toString());
                target.setStatus("Requisited");
                createdCount++;
            } else {
                target = existing;
                Long baseQty = Objects.requireNonNullElse(target.getQuantityRequested(), 0L);
                target.setQuantityRequested(reconcileMode ? incomingQty : baseQty + incomingQty);
                if (target.getRequisitionDate() == null || target.getRequisitionDate().isBlank()) {
                    target.setRequisitionDate(runDate.toString());
                }
                if (target.getStatus() == null || target.getStatus().isBlank()) {
                    target.setStatus("Requisited");
                }
                updatedCount++;
            }

            Suggestion suggestion = getLatestSuggestion(key.productId());
            if (suggestion != null) {
                target.setVendorSuggested(suggestion.vendorId());
                target.setPriceSuggested(suggestion.unitPrice());
            }

            requisitionOrderRepository.save(target);
        }

        for (ProjectInventoryView row : toTagRows) {
            if (tagInventorySource(row, cycle.getRequisitionCycleId())) {
                taggedInventoryCount++;
            }
        }

        if (createdCount + updatedCount > 0 && (cycle.getStatus() == null || "created".equalsIgnoreCase(cycle.getStatus()))) {
            cycle.setStatus("generated");
            requisitionCycleRepository.save(cycle);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("requisitionCycleId", cycle.getRequisitionCycleId());
        response.put("cycleStartDate", cyclePeriod.startDate().toString());
        response.put("cycleEndDate", cyclePeriod.endDate().toString());
        response.put("runDate", runDate.toString());
        response.put("createdCount", createdCount);
        response.put("updatedCount", updatedCount);
        response.put("taggedInventoryCount", taggedInventoryCount);
        response.put("mode", reconcileMode ? "reconcile" : "generate");
        response.put("status", cycle.getStatus());
        return response;
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

    private boolean isCompletedProject(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return Set.of("COMPLETE", "CLOSE").contains(normalized);
    }

    private boolean isEligibleInventoryRow(ProjectInventoryView row,
            RequisitionCycle cycle,
            CyclePeriod cyclePeriod,
            Set<String> activeProjectCodes,
            boolean reconcileMode) {
        if (row == null || row.getProductId() == null || row.getProjectCode() == null || row.getQuantity() == null) {
            return false;
        }
        if (!activeProjectCodes.contains(row.getProjectCode())) {
            return false;
        }

        Long rowCycleId = row.getRequisitionCycleId();
        if (rowCycleId != null) {
            if (!reconcileMode) {
                return false;
            }
            if (!Objects.equals(rowCycleId, cycle.getRequisitionCycleId())) {
                return false;
            }
        }

        LocalDate effectiveStart = resolveEffectiveStartDate(row);
        if (effectiveStart == null) {
            throw new IllegalArgumentException("Missing effective start date for inventory row " + row.getRowId());
        }
        return !effectiveStart.isBefore(cyclePeriod.startDate()) && !effectiveStart.isAfter(cyclePeriod.endDate());
    }

    private LocalDate resolveEffectiveStartDate(ProjectInventoryView row) {
        String status = normalizeStatus(row.getStatus());
        if ("not started".equals(status)) {
            return parseIsoDate(row.getStartDate(), "projectInventoryView.startDate rowId=" + row.getRowId());
        }
        LocalDate actualStart = parseIsoDate(row.getActualStartDate(),
                "projectInventoryView.actualStartDate rowId=" + row.getRowId());
        if (actualStart != null) {
            return actualStart;
        }
        return parseIsoDate(row.getStartDate(), "projectInventoryView.startDate rowId=" + row.getRowId());
    }

    private boolean tagInventorySource(ProjectInventoryView row, Long cycleId) {
        if (row.getInventoryType() == null || row.getInventoryId() == null || cycleId == null) {
            return false;
        }

        String type = row.getInventoryType().trim();
        Long id = row.getInventoryId();

        switch (type) {
            case "Asset" -> {
                Optional<ProjectAsset> record = projectAssetRepository.findById(id);
                if (record.isPresent()) {
                    ProjectAsset value = record.get();
                    if (!Objects.equals(value.getRequisitionCycleId(), cycleId)) {
                        value.setRequisitionCycleId(cycleId);
                        projectAssetRepository.save(value);
                    }
                    return true;
                }
            }
            case "Stock" -> {
                Optional<ProjectStock> record = projectStockRepository.findById(id);
                if (record.isPresent()) {
                    ProjectStock value = record.get();
                    if (!Objects.equals(value.getRequisitionCycleId(), cycleId)) {
                        value.setRequisitionCycleId(cycleId);
                        projectStockRepository.save(value);
                    }
                    return true;
                }
            }
            case "Bundle" -> {
                Long bundleRecordId = resolveBundleRecordId(row);
                if (bundleRecordId == null) {
                    return false;
                }
                Optional<ProjectBundle> record = projectBundleRepository.findById(bundleRecordId);
                if (record.isPresent()) {
                    ProjectBundle value = record.get();
                    if (!Objects.equals(value.getRequisitionCycleId(), cycleId)) {
                        value.setRequisitionCycleId(cycleId);
                        projectBundleRepository.save(value);
                    }
                    return true;
                }
            }
            case "StreamAsset" -> {
                Optional<ProjectStreamAsset> record = projectStreamAssetRepository.findById(id);
                if (record.isPresent()) {
                    ProjectStreamAsset value = record.get();
                    if (!Objects.equals(value.getRequisitionCycleId(), cycleId)) {
                        value.setRequisitionCycleId(cycleId);
                        projectStreamAssetRepository.save(value);
                    }
                    return true;
                }
            }
            case "StreamBundle" -> {
                Long streamBundleRecordId = resolveBundleRecordId(row);
                if (streamBundleRecordId == null) {
                    return false;
                }
                Optional<ProjectStreamBundle> record = projectStreamBundleRepository.findById(streamBundleRecordId);
                if (record.isPresent()) {
                    ProjectStreamBundle value = record.get();
                    if (!Objects.equals(value.getRequisitionCycleId(), cycleId)) {
                        value.setRequisitionCycleId(cycleId);
                        projectStreamBundleRepository.save(value);
                    }
                    return true;
                }
            }
            default -> {
            }
        }

        return false;
    }

    private Long resolveBundleRecordId(ProjectInventoryView row) {
        if (row.getRowId() == null || row.getRowId().isBlank()) {
            return null;
        }

        String[] parts = row.getRowId().split("-");
        if (parts.length < 3) {
            return null;
        }

        String qualifier = parts[parts.length - 1];
        try {
            return Long.valueOf(qualifier);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }

    private ProcurementCycleConfig getProcurementCycleConfig() {
        String cycleType = paramRepository.findById("procurementCycle")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse("MONTHLY")
                .toUpperCase(Locale.ROOT);

        if (!"MONTHLY".equals(cycleType)) {
            throw new IllegalArgumentException("Unsupported procurementCycle: " + cycleType + ". Only MONTHLY is supported.");
        }

        int cycleDay = paramRepository.findById("procurementCycleDay")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(Integer::parseInt)
                .orElse(1);

        if (cycleDay < 1 || cycleDay > 31) {
            throw new IllegalArgumentException("Invalid procurementCycleDay: " + cycleDay + ". Must be between 1 and 31.");
        }

        int leadTimeDays = paramRepository.findById("procurementLeadTimeDays")
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(Integer::parseInt)
                .orElse(0);

        if (leadTimeDays < 0) {
            throw new IllegalArgumentException("Invalid procurementLeadTimeDays: " + leadTimeDays + ". Must be zero or positive.");
        }

        return new ProcurementCycleConfig(cycleType, cycleDay, leadTimeDays);
    }

    private CyclePeriod resolveCyclePeriod(Long requisitionCycleId, LocalDate runDate, ProcurementCycleConfig config) {
        LocalDate earliestAllowedStart = runDate.plusDays(config.leadTimeDays());

        if (requisitionCycleId == null) {
            LocalDate cycleStart = resolveFirstEligibleMonthlyCycleStart(earliestAllowedStart, config.cycleDay());
            LocalDate cycleEnd = resolveNextMonthlyCycleStart(cycleStart, config.cycleDay()).minusDays(1);
            RequisitionCycle cycle = requisitionCycleRepository.findByRequisitionCycleDate(cycleStart.toString())
                    .orElseGet(() -> {
                        RequisitionCycle created = new RequisitionCycle();
                        created.setRequisitionCycleDate(cycleStart.toString());
                        created.setDateCreated(runDate.toString());
                        created.setStatus("created");
                        return requisitionCycleRepository.save(created);
                    });
            return new CyclePeriod(cycle, cycleStart, cycleEnd);
        }

        RequisitionCycle cycle = requisitionCycleRepository.findById(requisitionCycleId)
                .orElseThrow(() -> new IllegalArgumentException("Requisition cycle not found with id: " + requisitionCycleId));

        LocalDate cycleStart = parseIsoDate(cycle.getRequisitionCycleDate(),
                "requisitionCycleDate for requisitionCycleId=" + requisitionCycleId);
        if (cycleStart == null) {
            throw new IllegalArgumentException("Requisition cycle date is required for requisitionCycleId=" + requisitionCycleId);
        }
        if (cycleStart.isBefore(earliestAllowedStart)) {
            throw new IllegalArgumentException(
                    "Provided requisition cycle is too early. Cycle start must be on or after " + earliestAllowedStart
                    + " (runDate + procurementLeadTimeDays).");
        }

        LocalDate cycleEnd = resolveNextMonthlyCycleStart(cycleStart, config.cycleDay()).minusDays(1);
        return new CyclePeriod(cycle, cycleStart, cycleEnd);
    }

    private LocalDate resolveFirstEligibleMonthlyCycleStart(LocalDate earliestDate, int cycleDay) {
        int dayInMonth = Math.min(cycleDay, earliestDate.lengthOfMonth());
        LocalDate candidate = earliestDate.withDayOfMonth(dayInMonth);
        if (candidate.isBefore(earliestDate)) {
            LocalDate nextMonth = earliestDate.plusMonths(1);
            int nextMonthDay = Math.min(cycleDay, nextMonth.lengthOfMonth());
            return nextMonth.withDayOfMonth(nextMonthDay);
        }
        return candidate;
    }

    private LocalDate resolveNextMonthlyCycleStart(LocalDate cycleStart, int cycleDay) {
        LocalDate nextMonth = cycleStart.plusMonths(1);
        int day = Math.min(cycleDay, nextMonth.lengthOfMonth());
        return nextMonth.withDayOfMonth(day);
    }

    private LocalDate parseIsoDate(String value, String context) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format for " + context + ": " + value + ". Expected yyyy-MM-dd");
        }
    }

    private Suggestion getLatestSuggestion(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return null;
        }

        String productCode = productOpt.get().getProductCode();
        if (productCode == null || productCode.isBlank()) {
            return null;
        }

        List<PurchaseOrderItem> poItems = purchaseOrderItemRepository.findByProductCode(productCode.trim());
        if (poItems.isEmpty()) {
            return null;
        }

        List<String> orderIds = poItems.stream().map(PurchaseOrderItem::getOrderId).filter(Objects::nonNull).distinct().toList();
        Map<String, PurchaseOrder> poById = purchaseOrderRepository.findAllById(orderIds).stream()
                .collect(Collectors.toMap(PurchaseOrder::getOrderId, po -> po));

        PurchaseOrderItem latestItem = null;
        PurchaseOrder latestOrder = null;
        for (PurchaseOrderItem item : poItems) {
            PurchaseOrder order = poById.get(item.getOrderId());
            if (order == null) {
                continue;
            }
            if (order.getOrderStatus() != null && "CANCELLED".equalsIgnoreCase(order.getOrderStatus().trim())) {
                continue;
            }
            if (latestOrder == null) {
                latestOrder = order;
                latestItem = item;
                continue;
            }
            LocalDate currentDate = order.getOrderDate() == null ? LocalDate.MIN : order.getOrderDate().toLocalDate();
            LocalDate latestDate = latestOrder.getOrderDate() == null ? LocalDate.MIN : latestOrder.getOrderDate().toLocalDate();
            if (currentDate.isAfter(latestDate)) {
                latestOrder = order;
                latestItem = item;
            }
        }

        if (latestOrder == null || latestItem == null) {
            return null;
        }
        return new Suggestion(latestOrder.getVendorId(), latestItem.getUnitPrice());
    }

    private record ProcurementCycleConfig(String cycleType, int cycleDay, int leadTimeDays) {

    }

    private record CyclePeriod(RequisitionCycle cycle, LocalDate startDate, LocalDate endDate) {

    }

    private record RequisitionKey(String projectCode, Long productId) {

    }

    private record Suggestion(Long vendorId, Double unitPrice) {

    }
}
