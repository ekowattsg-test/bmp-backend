package com.hcteol.jwt.backend.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.RequisitionOrder;
import com.hcteol.jwt.backend.services.RequisitionOrderService;

@RestController
@RequestMapping("/api/requisitionorders")
public class RequisitionOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionOrderController.class);

    @Autowired
    private RequisitionOrderService requisitionOrderService;

    @GetMapping
    public List<RequisitionOrder> getRequisitionOrders(
            @RequestParam(required = false) Long requisitionCycleId,
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) Long productRequested,
            @RequestParam(required = false) String purchaseOrderId) {
        return requisitionOrderService.getRequisitionOrders(
                requisitionCycleId,
                projectCode,
                productRequested,
                purchaseOrderId);
    }

    @GetMapping("/ready-for-po")
    public List<RequisitionOrder> getRequisitionOrdersReadyForPurchaseOrder() {
        System.out.println("DEBUG READY-FOR-PO: controller endpoint invoked");
        LOG.info("DEBUG READY-FOR-PO: endpoint invoked");
        return requisitionOrderService.getRequisitionOrdersReadyForPurchaseOrder();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequisitionOrder> getRequisitionOrderById(@PathVariable Long id) {
        return requisitionOrderService.getRequisitionOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RequisitionOrder> createRequisitionOrder(@RequestBody RequisitionOrder requisitionOrder) {
        RequisitionOrder created = requisitionOrderService.addRequisitionOrder(requisitionOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateRequisitionOrders(@RequestParam(required = false) Long requisitionCycleId) {
        try {
            Map<String, Object> result = requisitionOrderService.generateRequisitionOrders(requisitionCycleId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/reconcile")
    public ResponseEntity<?> reconcileRequisitionOrders(@RequestParam(required = false) Long requisitionCycleId) {
        try {
            Map<String, Object> result = requisitionOrderService.reconcileRequisitionOrders(requisitionCycleId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/create-po")
    public ResponseEntity<?> createPurchaseOrdersFromSelectedRequisitions(
            @RequestBody List<RequisitionOrder> requisitionOrders) {
        try {
            Map<String, Object> result = requisitionOrderService
                    .createPurchaseOrdersFromSelectedRequisitions(requisitionOrders);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequisitionOrder> updateRequisitionOrder(@PathVariable Long id,
            @RequestBody RequisitionOrder details) {
        RequisitionOrder updated = requisitionOrderService.updateRequisitionOrder(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequisitionOrder(@PathVariable Long id) {
        requisitionOrderService.deleteRequisitionOrder(id);
        return ResponseEntity.noContent().build();
    }
}
