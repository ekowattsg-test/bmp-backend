package com.hcteol.jwt.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrder;
import com.hcteol.jwt.backend.entities.WorkSteps;
import com.hcteol.jwt.backend.entities.WorkStepsType;
import com.hcteol.jwt.backend.repositories.WorkOrderRepository;
import com.hcteol.jwt.backend.repositories.WorkStepsRepository;
import com.hcteol.jwt.backend.repositories.WorkStepsTypeRepository;

@Service
public class WorkStepsService {

    @Autowired
    private WorkStepsRepository workStepsRepository;

    @Autowired
    private WorkStepsTypeRepository workStepsTypeRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.WorkOrderDataRepository workOrderDataRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.StaffRepository staffRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.WorkOrderSubDataRepository workOrderSubDataRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.StockRepository stockRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.StockMovementRepository stockMovementRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private com.hcteol.jwt.backend.repositories.DeliveryOrderRepository deliveryOrderRepository;

    private static final Logger logger = LoggerFactory.getLogger(WorkStepsService.class);

    public WorkSteps addWorkStep(WorkSteps step) {
        return workStepsRepository.save(step);
    }

    public List<WorkSteps> getAllWorkSteps() {
        return workStepsRepository.findAll();
    }

    public java.util.List<WorkSteps> getWorkStepsByOrderId(String workOrderId) {
        return workStepsRepository.findByWorkOrderId(workOrderId);
    }

    public java.util.Optional<WorkSteps> getWorkStepById(Long id) {
        return workStepsRepository.findById(id);
    }

    public WorkSteps updateWorkStep(Long id, WorkSteps details) {
        var existing = workStepsRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderId(details.getWorkOrderId());
            existing.setStepNumber(details.getStepNumber());
            existing.setFromLocation(details.getFromLocation());
            existing.setToLocation(details.getToLocation());
            existing.setPhotos(details.getPhotos());
            existing.setStepStatus(details.getStepStatus());
            return workStepsRepository.save(existing);
        }
        return null;
    }

    public void deleteWorkStep(Long id) {
        workStepsRepository.deleteById(id);
    }

    private boolean isNoActEntity(String entity) {
        return entity != null && "noAct".equalsIgnoreCase(entity.trim());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Perform the current INPROGRESS step for the given work order. - finds the
     * step with status "INPROGRESS" - looks up the step definition via
     * WorkStepsType - performs action depending on endAction (no-op when empty)
     * - marks the step DONE and if all steps DONE, closes the work order
     */
    public void performCurrentStep(String workOrderId) {
        java.util.Optional<WorkOrder> woOpt = workOrderRepository.findById(workOrderId);
        if (woOpt.isEmpty()) {
            throw new java.util.NoSuchElementException("Work order not found: " + workOrderId);
        }
        WorkOrder wo = woOpt.get();

        logger.info("performCurrentStep invoked for workOrder {}", workOrderId);

        java.util.List<WorkSteps> steps = workStepsRepository.findByWorkOrderId(workOrderId);
        if (steps == null || steps.isEmpty()) {
            throw new IllegalStateException("No steps found for work order: " + workOrderId);
        }

        WorkSteps inProgress = null;
        for (WorkSteps s : steps) {
            if ("INPROGRESS".equalsIgnoreCase(s.getStepStatus())) {
                inProgress = s;
                break;
            }
        }

        if (inProgress == null) {
            throw new IllegalStateException("No INPROGRESS step found for work order: " + workOrderId);
        }

        java.util.Optional<WorkStepsType> defOpt = workStepsTypeRepository.findByWorkOrderTypeAndStepNumber(wo.getWorkOrderType(), inProgress.getStepNumber());
        if (defOpt.isEmpty()) {
            throw new IllegalStateException("Step definition not found for type " + wo.getWorkOrderType() + " step " + inProgress.getStepNumber());
        }
        WorkStepsType def = defOpt.get();
        boolean ignoreFromLocation = isNoActEntity(def.getFromEntity());
        boolean ignoreToLocation = isNoActEntity(def.getToEntity());

        logger.info("Resolved step definition: endAction='{}' fromEntity='{}' toEntity='{}' for workOrder {}", def.getEndAction(), def.getFromEntity(), def.getToEntity(), workOrderId);
        String endAction = def.getEndAction();
        logger.warn("Debug endAction raw='{}' trimmed='{}' equalsStockTx={}", endAction, endAction != null ? endAction.trim() : "<null>", "stock-tx".equalsIgnoreCase(endAction != null ? endAction.trim() : ""));
        logger.error("AFTER debug endAction for workOrder {} - about to evaluate stock-tx", workOrderId);
        try {
            String trimmed = endAction != null ? endAction.trim() : null;
            logger.warn("COND debug: endAction='{}' trimmed='{}' length={} equalsStockTx={}", endAction, trimmed, trimmed != null ? trimmed.length() : -1, "stock-tx".equalsIgnoreCase(trimmed != null ? trimmed : ""));
            if (trimmed != null) {
                logger.warn("COND debug: trimmed codepoints={}", java.util.Arrays.toString(trimmed.chars().toArray()));
            }
        } catch (Exception e) {
            logger.error("Error while dumping endAction diagnostics for workOrder {}: {}", workOrderId, e.getMessage(), e);
        }
        // compute trimmedAction and isStockTx unconditionally for later checks
        String trimmedAction = endAction != null ? endAction.trim() : "";
        boolean isStockTxEval = "stock-tx".equalsIgnoreCase(trimmedAction);
        logger.error("EVAL OUT debug: isStockTxEval={} trimmedAction='{}' endAction='{}' for workOrder {}", isStockTxEval, trimmedAction, endAction, workOrderId);
        // Framework: when endAction is empty, no action performed. Other endAction handlers to be added later.
        if (endAction != null && endAction.trim().length() > 0) {
            if ("staff-loc".equalsIgnoreCase(endAction.trim())) {
                // set staff.location for all staff referenced by WorkOrderData for this WO
                String targetLocation = inProgress.getToLocation();
                if (ignoreToLocation) {
                    logger.info("Skipping staff-loc location update for workOrder {} because toEntity is noAct", workOrderId);
                } else if (targetLocation == null || targetLocation.isBlank()) {
                    throw new IllegalStateException("Target location not set on step record for staff-loc action");
                } else {
                    java.util.List<com.hcteol.jwt.backend.entities.WorkOrderData> wodList = workOrderDataRepository.findByWorkOrderId(workOrderId);
                    for (com.hcteol.jwt.backend.entities.WorkOrderData wod : wodList) {
                        String staffId = wod.getStaffId();
                        if (staffId == null || staffId.isBlank()) {
                            logger.warn("WorkOrderData {} has no staffId, skipping", wod.getWorkOrderDataId());
                            continue;
                        }
                        var staffOpt = staffRepository.findById(staffId);
                        if (staffOpt.isPresent()) {
                            var staff = staffOpt.get();
                            staff.setLocation(targetLocation);
                            staffRepository.save(staff);
                            logger.info("Updated location for staff {} to {}", staffId, targetLocation);
                        } else {
                            logger.warn("Staff {} referenced by WorkOrderData {} not found", staffId, wod.getWorkOrderDataId());
                        }
                    }
                }
            } else {
                // TODO: handle other endAction values
            }
        }

        // Branch on trimmedAction for stock operations
        switch (trimmedAction.toLowerCase()) {
            case "stock-in": {
                String targetLocation = inProgress.getToLocation();
                if (!ignoreToLocation && (targetLocation == null || targetLocation.isBlank())) {
                    throw new IllegalStateException("Target location not set on step record for stock-in action");
                }

                // determine actionedBy: staff name of workOrder.workBy (try mobileNumber then staffId)
                String actionedBy = null;
                String workBy = wo.getWorkBy();
                if (workBy != null && !workBy.isBlank()) {
                    var sOpt = staffRepository.findByMobileNumber(workBy);
                    if (sOpt != null && sOpt.isPresent()) {
                        actionedBy = sOpt.get().getStaffName();
                    } else {
                        var sOpt2 = staffRepository.findById(workBy);
                        if (sOpt2.isPresent()) {
                            actionedBy = sOpt2.get().getStaffName();
                        }
                    }
                }
                if (actionedBy == null) {
                    actionedBy = "unknown";
                }

                java.util.List<com.hcteol.jwt.backend.entities.WorkOrderData> wodList = workOrderDataRepository.findByWorkOrderId(workOrderId);

                String poCandidate = null;
                if ("PO".equalsIgnoreCase(def.getFromEntity())) {
                    poCandidate = inProgress.getFromLocation();
                } else if ("PO".equalsIgnoreCase(def.getToEntity())) {
                    poCandidate = inProgress.getToLocation();
                }
                if (poCandidate != null) {
                    poCandidate = poCandidate.trim();
                }

                String doCandidate = null;
                if ("DO".equalsIgnoreCase(def.getFromEntity())) {
                    doCandidate = inProgress.getFromLocation();
                } else if ("DO".equalsIgnoreCase(def.getToEntity())) {
                    doCandidate = inProgress.getToLocation();
                }
                if (doCandidate != null) {
                    doCandidate = doCandidate.trim();
                }

                for (com.hcteol.jwt.backend.entities.WorkOrderData wod : wodList) {
                    java.util.List<com.hcteol.jwt.backend.entities.WorkOrderSubData> subList = workOrderSubDataRepository.findByWorkOrderDataId(wod.getWorkOrderDataId());
                    for (com.hcteol.jwt.backend.entities.WorkOrderSubData sub : subList) {
                        Long productId = sub.getProductId();
                        String stockCode = sub.getStockId();
                        Long qtyLong = sub.getSubQuantity();
                        int qty = qtyLong != null ? qtyLong.intValue() : 0;

                        com.hcteol.jwt.backend.entities.Stock stock = stockRepository.findByProductIdAndStockCode(productId, stockCode);
                        if (stock == null) {
                            stock = new com.hcteol.jwt.backend.entities.Stock();
                            stock.setProductId(productId);
                            stock.setStockCode(stockCode);
                            stock.setCreateDate(LocalDateTime.now().toString());
                            stock = stockRepository.save(stock);
                            logger.info("Created stock {} for product {}", stock.getStockId(), productId);
                        }

                        com.hcteol.jwt.backend.entities.StockMovement mv = new com.hcteol.jwt.backend.entities.StockMovement();
                        mv.setStockId(stock.getStockId());
                        mv.setMovementType("I");
                        mv.setQuantity(qty);
                        if (!ignoreToLocation && hasText(targetLocation)) {
                            mv.setLocation(targetLocation);
                        }
                        mv.setReference((poCandidate != null && !poCandidate.isBlank()) ? poCandidate : def.getFromEntity());
                        mv.setWorkOrderId(workOrderId);
                        mv.setRecordDate(LocalDateTime.now().toString());
                        mv.setActionBy(actionedBy);
                        stockMovementRepository.save(mv);
                        logger.info("Created stock movement {} for stock {} qty {}", mv.getMovementId(), stock.getStockId(), qty);
                    }
                }

                if ("PO".equalsIgnoreCase(def.getFromEntity())) {
                    if (poCandidate != null && !poCandidate.isBlank()) {
                        if (purchaseOrderRepository.existsById(poCandidate)) {
                            var poOpt = purchaseOrderRepository.findById(poCandidate);
                            if (poOpt.isPresent()) {
                                var po = poOpt.get();
                                po.setOrderStatus("RECEIVED");
                                po.setReceivedDate(new java.sql.Date(System.currentTimeMillis()));
                                purchaseOrderRepository.save(po);
                                logger.info("Marked PurchaseOrder {} as RECEIVED", poCandidate);
                            }
                        } else {
                            logger.warn("Candidate purchase order id '{}' not found, skipping PO status update", poCandidate);
                        }
                    } else {
                        logger.warn("No candidate Purchase Order id found to mark RECEIVED for workOrder {}", workOrderId);
                    }
                } else if ("DO".equalsIgnoreCase(def.getFromEntity())) {
                    if (doCandidate != null && !doCandidate.isBlank()) {
                        if (deliveryOrderRepository.existsById(doCandidate)) {
                            var doOpt = deliveryOrderRepository.findById(doCandidate);
                            if (doOpt.isPresent()) {
                                var d = doOpt.get();
                                d.setOrderStatus("DELIVERED");
                                d.setDeliveredDate(new java.sql.Date(System.currentTimeMillis()));
                                deliveryOrderRepository.save(d);
                                logger.info("Marked DeliveryOrder {} as DELIVERED", doCandidate);
                            }
                        } else {
                            logger.warn("Candidate delivery order id '{}' not found, skipping DO status update", doCandidate);
                        }
                    } else {
                        logger.warn("No candidate Delivery Order id found to mark DELIVERED for workOrder {}", workOrderId);
                    }
                }
                break;
            }

            case "stock-out": {
                String fromLocation = inProgress.getFromLocation();
                if (!ignoreFromLocation && (fromLocation == null || fromLocation.isBlank())) {
                    throw new IllegalStateException("Source location (fromLocation) not set on step record for stock-out action");
                }

                // actionBy = the operator who created/issued the work order
                String actionedByOut = wo.getIssuedBy();
                if (actionedByOut == null || actionedByOut.isBlank()) {
                    actionedByOut = "unknown";
                }

                // reference = the work order that generated the movement
                String refCandidate = workOrderId;

                java.util.List<com.hcteol.jwt.backend.entities.WorkOrderData> wodList2 = workOrderDataRepository.findByWorkOrderId(workOrderId);
                for (com.hcteol.jwt.backend.entities.WorkOrderData wod : wodList2) {
                    java.util.List<com.hcteol.jwt.backend.entities.WorkOrderSubData> subList = workOrderSubDataRepository.findByWorkOrderDataId(wod.getWorkOrderDataId());
                    for (com.hcteol.jwt.backend.entities.WorkOrderSubData sub : subList) {
                        Long productId = sub.getProductId();
                        String stockCode = sub.getStockId();
                        Long qtyLong = sub.getSubQuantity();
                        int qty = qtyLong != null ? qtyLong.intValue() : 0;

                        com.hcteol.jwt.backend.entities.Stock stock = stockRepository.findByProductIdAndStockCode(productId, stockCode);
                        if (stock == null) {
                            throw new IllegalStateException("Stock not found for product " + productId + " and code " + stockCode + " during stock-out for workOrder " + workOrderId);
                        }

                        com.hcteol.jwt.backend.entities.StockMovement mv = new com.hcteol.jwt.backend.entities.StockMovement();
                        mv.setStockId(stock.getStockId());
                        mv.setMovementType("O");
                        mv.setQuantity(qty);
                        if (!ignoreFromLocation && hasText(fromLocation)) {
                            mv.setLocation(fromLocation);
                        }
                        mv.setReference(refCandidate);
                        mv.setWorkOrderId(workOrderId);
                        mv.setRecordDate(LocalDateTime.now().toString());
                        mv.setActionBy(actionedByOut);
                        stockMovementRepository.save(mv);
                        logger.info("Created stock-out movement {} for stock {} qty {}", mv.getMovementId(), stock.getStockId(), qty);
                    }
                }
                break;
            }

            case "transfer-out": {
                String fromLocationOut = inProgress.getFromLocation();
                if (!ignoreFromLocation && (fromLocationOut == null || fromLocationOut.isBlank())) {
                    throw new IllegalStateException("Source location (fromLocation) not set on step record for transfer-out action");
                }

                String actionedByTransferOut = wo.getIssuedBy();
                if (actionedByTransferOut == null || actionedByTransferOut.isBlank()) {
                    actionedByTransferOut = "unknown";
                }

                String refCandidateTransferOut = workOrderId;
                String doCandidateTransferOut = null;

                // Frontend stores a DO-linked location as "DO_ID|locationCode"
                String fromLocationRaw = inProgress.getFromLocation();
                if (fromLocationRaw != null && !fromLocationRaw.isBlank() && fromLocationRaw.contains("|")) {
                    String[] parts = fromLocationRaw.split("\\|", 2);
                    doCandidateTransferOut = parts[0];
                    fromLocationOut = parts[1];
                }

                if (doCandidateTransferOut != null && !doCandidateTransferOut.isBlank()) {
                    doCandidateTransferOut = doCandidateTransferOut.trim();
                    refCandidateTransferOut = doCandidateTransferOut;
                }

                java.util.List<com.hcteol.jwt.backend.entities.WorkOrderData> wodListOut = workOrderDataRepository.findByWorkOrderId(workOrderId);
                for (com.hcteol.jwt.backend.entities.WorkOrderData wod : wodListOut) {
                    java.util.List<com.hcteol.jwt.backend.entities.WorkOrderSubData> subListOut = workOrderSubDataRepository.findByWorkOrderDataId(wod.getWorkOrderDataId());
                    for (com.hcteol.jwt.backend.entities.WorkOrderSubData sub : subListOut) {
                        Long productId = sub.getProductId();
                        String stockCode = sub.getStockId();
                        Long qtyLong = sub.getSubQuantity();
                        int qty = qtyLong != null ? qtyLong.intValue() : 0;

                        com.hcteol.jwt.backend.entities.Stock stock = stockRepository.findByProductIdAndStockCode(productId, stockCode);
                        if (stock == null) {
                            throw new IllegalStateException("Stock not found for product " + productId + " and code " + stockCode + " during transfer-out for workOrder " + workOrderId);
                        }

                        com.hcteol.jwt.backend.entities.StockMovement mv = new com.hcteol.jwt.backend.entities.StockMovement();
                        mv.setStockId(stock.getStockId());
                        mv.setMovementType("G");
                        mv.setQuantity(qty);
                        if (!ignoreFromLocation && hasText(fromLocationOut)) {
                            mv.setLocation(fromLocationOut);
                        }
                        mv.setReference(refCandidateTransferOut);
                        mv.setWorkOrderId(workOrderId);
                        mv.setRecordDate(LocalDateTime.now().toString());
                        mv.setActionBy(actionedByTransferOut);
                        stockMovementRepository.save(mv);
                        logger.info("Created transfer-out movement {} for stock {} qty {}", mv.getMovementId(), stock.getStockId(), qty);
                    }
                }

                if (doCandidateTransferOut != null && !doCandidateTransferOut.isBlank()) {
                    if (deliveryOrderRepository.existsById(doCandidateTransferOut)) {
                        var doOpt = deliveryOrderRepository.findById(doCandidateTransferOut);
                        if (doOpt.isPresent()) {
                            var d = doOpt.get();
                            d.setOrderStatus("IN_TRANSIT");
                            deliveryOrderRepository.save(d);
                            logger.info("Marked DeliveryOrder {} as IN_TRANSIT", doCandidateTransferOut);
                        }
                    } else {
                        logger.warn("Candidate delivery order id '{}' not found, skipping DO status update", doCandidateTransferOut);
                    }
                }
                break;
            }

            case "transfer-in": {
                String targetLocationIn = inProgress.getToLocation();
                if (!ignoreToLocation && (targetLocationIn == null || targetLocationIn.isBlank())) {
                    throw new IllegalStateException("Target location not set on step record for transfer-in action");
                }

                String actionedByTransferIn = wo.getIssuedBy();
                if (actionedByTransferIn == null || actionedByTransferIn.isBlank()) {
                    actionedByTransferIn = "unknown";
                }

                String refCandidateTransferIn = workOrderId;
                String doCandidateTransferIn = null;

                // Frontend stores a DO-linked location as "DO_ID|locationCode"
                String fromLocationRawIn = inProgress.getFromLocation();
                if (fromLocationRawIn != null && !fromLocationRawIn.isBlank() && fromLocationRawIn.contains("|")) {
                    String[] parts = fromLocationRawIn.split("\\|", 2);
                    doCandidateTransferIn = parts[0];
                }

                if (doCandidateTransferIn != null && !doCandidateTransferIn.isBlank()) {
                    doCandidateTransferIn = doCandidateTransferIn.trim();
                    refCandidateTransferIn = doCandidateTransferIn;
                }

                java.util.List<com.hcteol.jwt.backend.entities.WorkOrderData> wodListIn = workOrderDataRepository.findByWorkOrderId(workOrderId);
                for (com.hcteol.jwt.backend.entities.WorkOrderData wod : wodListIn) {
                    java.util.List<com.hcteol.jwt.backend.entities.WorkOrderSubData> subListIn = workOrderSubDataRepository.findByWorkOrderDataId(wod.getWorkOrderDataId());
                    for (com.hcteol.jwt.backend.entities.WorkOrderSubData sub : subListIn) {
                        Long productId = sub.getProductId();
                        String stockCode = sub.getStockId();
                        Long qtyLong = sub.getSubQuantity();
                        int qty = qtyLong != null ? qtyLong.intValue() : 0;

                        com.hcteol.jwt.backend.entities.Stock stock = stockRepository.findByProductIdAndStockCode(productId, stockCode);
                        if (stock == null) {
                            stock = new com.hcteol.jwt.backend.entities.Stock();
                            stock.setProductId(productId);
                            stock.setStockCode(stockCode);
                            stock.setCreateDate(LocalDateTime.now().toString());
                            stock = stockRepository.save(stock);
                            logger.info("Created stock {} for product {} during transfer-in", stock.getStockId(), productId);
                        }

                        com.hcteol.jwt.backend.entities.StockMovement mv = new com.hcteol.jwt.backend.entities.StockMovement();
                        mv.setStockId(stock.getStockId());
                        mv.setMovementType("C");
                        mv.setQuantity(qty);
                        if (!ignoreToLocation && hasText(targetLocationIn)) {
                            mv.setLocation(targetLocationIn);
                        }
                        mv.setReference(refCandidateTransferIn);
                        mv.setWorkOrderId(workOrderId);
                        mv.setRecordDate(LocalDateTime.now().toString());
                        mv.setActionBy(actionedByTransferIn);
                        stockMovementRepository.save(mv);
                        logger.info("Created transfer-in movement {} for stock {} qty {}", mv.getMovementId(), stock.getStockId(), qty);
                    }
                }

                if (doCandidateTransferIn != null && !doCandidateTransferIn.isBlank()) {
                    if (deliveryOrderRepository.existsById(doCandidateTransferIn)) {
                        var doOpt = deliveryOrderRepository.findById(doCandidateTransferIn);
                        if (doOpt.isPresent()) {
                            var d = doOpt.get();
                            d.setOrderStatus("DELIVERED");
                            d.setDeliveredDate(new java.sql.Date(System.currentTimeMillis()));
                            deliveryOrderRepository.save(d);
                            logger.info("Marked DeliveryOrder {} as DELIVERED", doCandidateTransferIn);
                        }
                    } else {
                        logger.warn("Candidate delivery order id '{}' not found, skipping DO status update", doCandidateTransferIn);
                    }
                }
                break;
            }

            case "stock-tx": {
                try {
                    logger.warn("ENTER stock-tx block for workOrder {}", workOrderId);
                    logger.info("Begin stock-tx for workOrder {}", workOrderId);
                    logger.debug("Load work order {}", workOrderId);
                    logger.debug("Found INPROGRESS step {} (stepNumber={}) for workOrder {}", inProgress.getWorkStepsId(), inProgress.getStepNumber(), workOrderId);

                    String fromLocationTx = inProgress.getFromLocation();
                    String toLocationTx = inProgress.getToLocation();
                    logger.info("EndAction=stock-tx, fromLocation='{}', toLocation='{}'", fromLocationTx, toLocationTx);

                    // If the entity for from/to is a worker, expand the stored staff id/mobile into staff name
                    String effectiveFromLocation = fromLocationTx;
                    if (def.getFromEntity() != null && "worker".equalsIgnoreCase(def.getFromEntity().trim())) {
                        String raw = fromLocationTx;
                        if (raw != null && !raw.isBlank()) {
                            var sOptA = staffRepository.findByMobileNumber(raw);
                            if (sOptA != null && sOptA.isPresent()) {
                                effectiveFromLocation = sOptA.get().getStaffName();
                            } else {
                                var sOptB = staffRepository.findById(raw);
                                if (sOptB.isPresent()) {
                                    effectiveFromLocation = sOptB.get().getStaffName();
                                }
                            }
                        }
                    }

                    String effectiveToLocation = toLocationTx;
                    if (def.getToEntity() != null && "worker".equalsIgnoreCase(def.getToEntity().trim())) {
                        String raw = toLocationTx;
                        if (raw != null && !raw.isBlank()) {
                            var sOptA = staffRepository.findByMobileNumber(raw);
                            if (sOptA != null && sOptA.isPresent()) {
                                effectiveToLocation = sOptA.get().getStaffName();
                            } else {
                                var sOptB = staffRepository.findById(raw);
                                if (sOptB.isPresent()) {
                                    effectiveToLocation = sOptB.get().getStaffName();
                                }
                            }
                        }
                    }
                    if (!ignoreFromLocation && (fromLocationTx == null || fromLocationTx.isBlank())) {
                        logger.error("Aborting stock-tx: missing fromLocation for workOrder {}", workOrderId);
                        throw new IllegalStateException("Source location (fromLocation) not set on step record for stock-tx action");
                    }
                    if (!ignoreToLocation && (toLocationTx == null || toLocationTx.isBlank())) {
                        logger.error("Aborting stock-tx: missing toLocation for workOrder {}", workOrderId);
                        throw new IllegalStateException("Target location (toLocation) not set on step record for stock-tx action");
                    }

                    logger.debug("Resolving actionedBy for workOrder.workBy='{}'", wo.getWorkBy());
                    String actionedByTx = null;
                    String workByTx = wo.getWorkBy();
                    if (workByTx != null && !workByTx.isBlank()) {
                        var sOpt = staffRepository.findByMobileNumber(workByTx);
                        if (sOpt != null && sOpt.isPresent()) {
                            actionedByTx = sOpt.get().getStaffName();
                        } else {
                            var sOpt2 = staffRepository.findById(workByTx);
                            if (sOpt2.isPresent()) {
                                actionedByTx = sOpt2.get().getStaffName();
                            }
                        }
                    }
                    if (actionedByTx == null) {
                        actionedByTx = "unknown";
                    }
                    logger.info("ActionedBy resolved to '{}'", actionedByTx);

                    String txRef = "TX-" + workOrderId;
                    logger.info("Transaction reference set to '{}'", txRef);

                    java.util.List<com.hcteol.jwt.backend.entities.WorkOrderData> wodListTx = workOrderDataRepository.findByWorkOrderId(workOrderId);
                    if (wodListTx == null) {
                        wodListTx = java.util.Collections.emptyList();
                    }
                    logger.debug("Loading WorkOrderData for workOrder {} (count={})", workOrderId, wodListTx != null ? wodListTx.size() : 0);

                    int processedCount = 0;
                    int movementsCreatedCount = 0;
                    int wodIndex = 0;
                    for (com.hcteol.jwt.backend.entities.WorkOrderData wod : wodListTx) {
                        wodIndex++;
                        logger.debug("Processing WorkOrderData {} (workOrderDataId={})", wodIndex, wod.getWorkOrderDataId());
                        java.util.List<com.hcteol.jwt.backend.entities.WorkOrderSubData> subListTx = workOrderSubDataRepository.findByWorkOrderDataId(wod.getWorkOrderDataId());
                        int subIndex = 0;
                        for (com.hcteol.jwt.backend.entities.WorkOrderSubData sub : subListTx) {
                            subIndex++;
                            Long productId = sub.getProductId();
                            String stockCode = sub.getStockId();
                            Long qtyLong = sub.getSubQuantity();
                            int qty = qtyLong != null ? qtyLong.intValue() : 0;
                            logger.debug("Found WorkOrderSubData {} (productId={}, stockCode={}, qty={})", subIndex, productId, stockCode, qty);

                            logger.debug("Checking stock exists for productId={} stockCode={}", productId, stockCode);
                            com.hcteol.jwt.backend.entities.Stock stock = stockRepository.findByProductIdAndStockCode(productId, stockCode);
                            if (stock == null) {
                                logger.error("Stock missing for productId={} stockCode={} — aborting stock-tx for workOrder {}", productId, stockCode, workOrderId);
                                throw new IllegalStateException("Stock not found for product " + productId + " and code " + stockCode + " during stock-tx for workOrder " + workOrderId);
                            }
                            logger.info("Stock found: stockId={} for productId={} stockCode={}", stock.getStockId(), productId, stockCode);

                            Long outMovementId = null;
                            Long inMovementId = null;
                            if (!ignoreFromLocation) {
                                logger.info("Creating transfer-out movement (G) for stockId={} qty={} location='{}' reference='{}' actionBy='{}'", stock.getStockId(), qty, fromLocationTx, txRef, actionedByTx);
                                com.hcteol.jwt.backend.entities.StockMovement mvOut = new com.hcteol.jwt.backend.entities.StockMovement();
                                mvOut.setStockId(stock.getStockId());
                                mvOut.setMovementType("G");
                                mvOut.setQuantity(qty);
                                mvOut.setLocation(effectiveFromLocation);
                                mvOut.setReference(txRef);
                                mvOut.setWorkOrderId(workOrderId);
                                mvOut.setRecordDate(LocalDateTime.now().toString());
                                mvOut.setActionBy(actionedByTx);
                                logger.debug("Saving StockMovement OUT for stockId={} (will set workOrderId={})", stock.getStockId(), workOrderId);
                                stockMovementRepository.save(mvOut);
                                movementsCreatedCount++;
                                outMovementId = mvOut.getMovementId();
                                logger.info("Saved OUT movement id={} for stockId={} qty={}", mvOut.getMovementId(), stock.getStockId(), qty);
                            } else {
                                logger.info("Skipping transfer-out movement for workOrder {} because fromEntity is noAct", workOrderId);
                            }

                            if (!ignoreToLocation) {
                                logger.info("Creating transfer-in movement (C) for stockId={} qty={} location='{}' reference='{}' actionBy='{}'", stock.getStockId(), qty, toLocationTx, txRef, actionedByTx);
                                com.hcteol.jwt.backend.entities.StockMovement mvIn = new com.hcteol.jwt.backend.entities.StockMovement();
                                mvIn.setStockId(stock.getStockId());
                                mvIn.setMovementType("C");
                                mvIn.setQuantity(qty);
                                mvIn.setLocation(effectiveToLocation);
                                mvIn.setReference(txRef);
                                mvIn.setWorkOrderId(workOrderId);
                                mvIn.setRecordDate(LocalDateTime.now().toString());
                                mvIn.setActionBy(actionedByTx);
                                logger.debug("Saving StockMovement IN for stockId={} (will set workOrderId={})", stock.getStockId(), workOrderId);
                                stockMovementRepository.save(mvIn);
                                movementsCreatedCount++;
                                inMovementId = mvIn.getMovementId();
                                logger.info("Saved IN movement id={} for stockId={} qty={}", mvIn.getMovementId(), stock.getStockId(), qty);
                            } else {
                                logger.info("Skipping transfer-in movement for workOrder {} because toEntity is noAct", workOrderId);
                            }

                            logger.debug("Processed subdata {}: created OUT={} IN={} for stockId={}", subIndex, outMovementId, inMovementId, stock.getStockId());
                            processedCount++;
                        }
                    }

                    logger.info("Completed stock-tx for workOrder {}: total subitems processed={}, total movements created={}", workOrderId, processedCount, movementsCreatedCount);

                    // After stock transfer is complete, mark Delivery Order as DELIVERED when
                    // any step definition in this work order uses fromEntity=DO.
                    String deliveryOrderId = null;
                    for (WorkSteps step : steps) {
                        if (step.getStepNumber() == null) {
                            continue;
                        }
                        java.util.Optional<WorkStepsType> stepDefOpt = workStepsTypeRepository
                                .findByWorkOrderTypeAndStepNumber(wo.getWorkOrderType(), step.getStepNumber());
                        if (stepDefOpt.isEmpty()) {
                            continue;
                        }
                        WorkStepsType stepDef = stepDefOpt.get();
                        if (stepDef.getFromEntity() != null
                                && "DO".equalsIgnoreCase(stepDef.getFromEntity().trim())
                                && hasText(step.getFromLocation())) {
                            deliveryOrderId = step.getFromLocation().trim();
                            break;
                        }
                    }

                    if (hasText(deliveryOrderId)) {
                        if (deliveryOrderRepository.existsById(deliveryOrderId)) {
                            var doOpt = deliveryOrderRepository.findById(deliveryOrderId);
                            if (doOpt.isPresent()) {
                                var d = doOpt.get();
                                d.setOrderStatus("DELIVERED");
                                d.setDeliveredDate(new java.sql.Date(System.currentTimeMillis()));
                                deliveryOrderRepository.save(d);
                                logger.info("Marked DeliveryOrder {} as DELIVERED after stock-tx", deliveryOrderId);
                            }
                        } else {
                            logger.warn("Candidate delivery order id '{}' not found, skipping DO status update after stock-tx", deliveryOrderId);
                        }
                    } else {
                        logger.info("No step definition with fromEntity=DO found for workOrder {}; skipping DO status update after stock-tx", workOrderId);
                    }
                } catch (Exception e) {
                    logger.error("Exception during stock-tx for workOrder {}: {}", workOrderId, e.getMessage(), e);
                    throw e;
                }
                break;
            }

            case "stock-loc": {
                String newLocation = inProgress.getToLocation();
                if (ignoreToLocation) {
                    logger.info("Skipping stock-loc update for workOrder {} because toEntity is noAct", workOrderId);
                    break;
                }
                if (newLocation == null || newLocation.isBlank()) {
                    throw new IllegalStateException("Target location (toLocation) not set on step record for stock-loc action");
                }

                String expectedOldLocation = inProgress.getFromLocation();
                if (!ignoreFromLocation && (expectedOldLocation == null || expectedOldLocation.isBlank())) {
                    throw new IllegalStateException("Source location (fromLocation) not set on step record; cannot filter stock movements for stock-loc action");
                }

                java.util.List<com.hcteol.jwt.backend.entities.StockMovement> movements = stockMovementRepository.findByWorkOrderId(workOrderId);
                if (movements == null || movements.isEmpty()) {
                    logger.info("No stock movements found for work order {}", workOrderId);
                } else {
                    for (com.hcteol.jwt.backend.entities.StockMovement mv : movements) {
                        if (ignoreFromLocation || expectedOldLocation.equals(mv.getLocation())) {
                            mv.setLocation(newLocation);
                            stockMovementRepository.save(mv);
                            logger.info("Updated StockMovement {} location -> {}", mv.getMovementId(), newLocation);
                        } else {
                            logger.debug("Skipping StockMovement {} because location '{}' != expected '{}'", mv.getMovementId(), mv.getLocation(), expectedOldLocation);
                        }
                    }
                }
                break;
            }

            default:
                // no-op for other actions
                break;
        }
        // mark step DONE
        inProgress.setStepStatus("DONE");
        workStepsRepository.save(inProgress);

        // if all steps are DONE, close work order
        boolean allDone = true;
        for (WorkSteps s : steps) {
            if (!"DONE".equalsIgnoreCase(s.getStepStatus())) {
                allDone = false;
                break;
            }
        }
        if (allDone) {
            wo.setWorkOrderStatus("CLOSED");
            workOrderRepository.save(wo);
        }
    }
}
