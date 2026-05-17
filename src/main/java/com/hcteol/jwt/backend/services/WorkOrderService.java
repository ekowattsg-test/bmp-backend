package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrder;
import com.hcteol.jwt.backend.repositories.WorkOrderRepository;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private com.hcteol.jwt.backend.services.DocumentSeqService documentSeqService;

    public WorkOrder addWorkOrder(WorkOrder workOrder) {
        // If entity id absent, obtain a WO sequence and set workOrderId to "WO-<seq>"
        if (workOrder.getWorkOrderId() == null || workOrder.getWorkOrderId().trim().length() == 0) {
            String token = UUID.randomUUID().toString();
            Long seq = documentSeqService.getNextSeq("WO", token);
            workOrder.setWorkOrderId("WO-" + seq);
        }
        return workOrderRepository.save(workOrder);
    }

    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    public java.util.Optional<WorkOrder> getWorkOrderById(String id) {
        return workOrderRepository.findById(id);
    }

    public WorkOrder updateWorkOrder(String id, WorkOrder details) {
        var existing = workOrderRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderType(details.getWorkOrderType());
            existing.setWorkDescription(details.getWorkDescription());
            existing.setIssuedBy(details.getIssuedBy());
            existing.setWorkOrderDate(details.getWorkOrderDate());
            existing.setWorkBy(details.getWorkBy());
            existing.setWorkOrderStatus(details.getWorkOrderStatus());
            return workOrderRepository.save(existing);
        }
        return null;
    }

    public void deleteWorkOrder(String id) {
        workOrderRepository.deleteById(id);
    }
}
